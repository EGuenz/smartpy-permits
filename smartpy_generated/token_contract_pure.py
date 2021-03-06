import smartpy as sp
import smartpy_michelson as mi
import json

class FA12(sp.Contract):
    def __init__(self, admin):
        with open('metadata/metadata.json', 'r') as f:
          #loads then dumps to confirm correctly formatted json
          metadata = json.dumps(json.load(f))
          self.init(paused=False, balances=sp.big_map(tvalue=sp.TRecord(approvals=sp.TMap(sp.TAddress, sp.TNat), balance=sp.TNat)), administrator=admin, totalSupply=0,
                    permits=sp.big_map(tkey=sp.TPair(sp.TAddress, sp.TBytes), tvalue=sp.TTimestamp), user_expiries=sp.big_map(tkey=sp.TAddress, tvalue=sp.TOption(sp.TNat)),
                    permit_expiries=sp.big_map(tkey=sp.TPair(sp.TAddress, sp.TBytes), tvalue=sp.TOption(sp.TNat)), counter=0,
                    default_expiry = 50000, max_expiry = 2628000, metadata=sp.big_map(l={"": sp.bytes_of_string("tezos-storage:md-json"), "md-json": sp.bytes_of_string(metadata)}))

    @sp.entry_point
    def transfer(self, params):
        sp.set_type(params, sp.TRecord(from_=sp.TAddress, to_=sp.TAddress,
                                       value=sp.TNat)).layout(("from_ as from", ("to_ as to", "value")))
        sender = sp.local("sender", sp.sender)
        with sp.if_((self.transfer_presigned(params))):
            # Setting sender.value to from_ so call to transfer_helper will be authorized
            sender.value = params.from_
        sp.verify((sender.value == self.data.administrator) |
                  (~self.data.paused &
                   ((params.from_ == sender.value) |
                    (self.data.balances[params.from_].approvals[sender.value] >= params.value))))
        self.addAddressIfNecessary(params.to_)
        sp.verify(self.data.balances[params.from_].balance >= params.value)
        self.data.balances[params.from_].balance = sp.as_nat(
            self.data.balances[params.from_].balance - params.value)
        self.data.balances[params.to_].balance += params.value
        with sp.if_(((params.from_ != sender.value) & (self.data.administrator != sender.value))):
            self.data.balances[params.from_].approvals[sender.value] = sp.as_nat(
                self.data.balances[params.from_].approvals[sender.value] - params.value)

    @sp.entry_point
    def delete_permits(self, permit_keys):
        sp.set_type(permit_keys, sp.TList(sp.TPair(sp.TAddress, sp.TBytes)))
        effective_expiry = sp.local("effective_expiry", 0)
        with sp.for_('permit_key', permit_keys) as permit_key:
            permit_exists = self.data.permits.contains(permit_key)
            sp.verify(permit_exists, sp.pair(
                "NO_PERMIT_TO_DELETE", permit_key))
            effective_expiry = self.getEffectiveExpiry(permit_key)
            permit_submission_timestamp = self.data.permits[permit_key]
            sp.verify(sp.as_nat(sp.now - permit_submission_timestamp) >= effective_expiry,
                      sp.pair("PERMIT_NOT_EXPIRED", permit_key))
            self.delete_permit(permit_key)

    def delete_permit(self, permit_key):
        sp.set_type(permit_key, sp.TPair(sp.TAddress, sp.TBytes))
        with sp.if_(self.data.permits.contains(permit_key)):
            del self.data.permits[permit_key]
        with sp.if_(self.data.permit_expiries.contains(permit_key)):
            del self.data.permit_expiries[permit_key]

    @sp.sub_entry_point
    def transfer_presigned(self, params):
        sp.set_type(params, sp.TRecord(
            from_=sp.TAddress, to_=sp.TAddress, value=sp.TNat))
        params_hash = sp.blake2b(sp.pack(params))
        #unsigned = sp.blake2b(mi.operator("SELF; ADDRESS; CHAIN_ID; PAIR; PAIR; PACK", [sp.TPair(sp.TNat, sp.TBytes)], [sp.TBytes])(sp.pair(self.data.counter, params_hash)))
        permit_key = sp.pair(params.from_, params_hash)
        effective_expiry = sp.local("effective_expiry", 0)
        with sp.if_(self.data.permits.contains(permit_key)):
            permit_submission_timestamp = self.data.permits[permit_key]
            with sp.if_(self.data.permit_expiries.contains(permit_key) & self.data.permit_expiries[permit_key].is_some()):
                effective_expiry.value = self.data.permit_expiries[permit_key].open_some()
            with sp.else_():
                with sp.if_(self.data.user_expiries.contains(params.from_) & self.data.user_expiries[params.from_].is_some()):
                    effective_expiry.value = self.data.user_expiries[params.from_].open_some()
                with sp.else_():
                    effective_expiry.value = self.data.default_expiry
            # Deleting permit regardless of whether or not its expired
            with sp.if_(sp.as_nat(sp.now - permit_submission_timestamp) >= effective_expiry.value):
                # Expired
                self.delete_permit(permit_key)
                sp.result(sp.bool(False))
            with sp.else_():
                self.delete_permit(permit_key)
                sp.result(sp.bool(True))
        with sp.else_():
            sp.result(sp.bool(False))

    @sp.entry_point
    def permit(self, params):
        sp.set_type(params, sp.TList(
            sp.TPair(sp.TKey, sp.TPair(sp.TSignature, sp.TBytes))))
        sp.verify(~self.data.paused)
        with sp.for_('permit', params) as permit:
            params_hash = sp.snd(sp.snd(permit))
            unsigned = sp.pack(sp.pair(sp.pair(sp.chain_id, sp.self_address), sp.pair(
                self.data.counter, params_hash)))
            pk_address = sp.to_address(
                sp.implicit_account(sp.hash_key(sp.fst(permit))))
            permit_key = sp.pair(pk_address, params_hash)
            permit_exists = self.data.permits.contains(permit_key)
            effective_expiry = self.getEffectiveExpiry(
                sp.pair(pk_address, params_hash))
            permit_submission_timestamp = self.data.permits[permit_key]
            sp.verify(~ (permit_exists & (sp.as_nat(sp.now - permit_submission_timestamp) < effective_expiry)),
                      sp.pair("DUP_PERMIT", params_hash))
            sp.verify(sp.check_signature(sp.fst(permit), sp.fst(
                sp.snd(permit)), unsigned), sp.pair("MISSIGNED", unsigned))
            self.data.permits[sp.pair(pk_address, params_hash)] = sp.now
            self.data.counter = self.data.counter + 1

    @sp.sub_entry_point
    def getEffectiveExpiry(self, params):
        sp.set_type(params, sp.TPair(sp.TAddress, sp.TBytes))
        address = sp.fst(params)
        with sp.if_(self.data.permit_expiries.contains(params) & self.data.permit_expiries[params].is_some()):
            permit_expiry = self.data.permit_expiries[params].open_some()
            sp.result(permit_expiry)
        with sp.else_():
            with sp.if_(self.data.user_expiries.contains(address) & self.data.user_expiries[address].is_some()):
                user_expiry = self.data.user_expiries[address].open_some()
                sp.result(user_expiry)
            with sp.else_():
                sp.result(self.data.default_expiry)

    @sp.entry_point
    def setExpiry(self, params):
        sp.set_type(params, sp.TRecord(address=sp.TAddress, seconds=sp.TNat, permit=sp.TOption(
            sp.TBytes))).layout(("address", ("seconds", "permit")))
        sp.verify(~self.data.paused)
        sp.verify(params.seconds <= self.data.max_expiry, "MAX_SECONDS_EXCEEDED")
        sp.verify_equal(params.address, sp.sender, message="NOT_AUTHORIZED")
        with sp.if_(params.permit.is_some()):
            some_permit = params.permit.open_some()
            sp.verify(self.data.permits.contains(
                sp.pair(params.address, some_permit)), "PERMIT_NONEXISTENT")
            permit_submission_timestamp = self.data.permits[sp.pair(
                params.address, some_permit)]
            with sp.if_(self.data.permit_expiries.contains(sp.pair(params.address, some_permit)) & self.data.permit_expiries[sp.pair(params.address, some_permit)].is_some()):
                permit_expiry = self.data.permit_expiries[sp.pair(
                    params.address, some_permit)].open_some()
                sp.verify(sp.as_nat(sp.now - permit_submission_timestamp)
                          < permit_expiry, "PERMIT_REVOKED")
            with sp.else_():
                with sp.if_(self.data.user_expiries.contains(params.address) & self.data.user_expiries[params.address].is_some()):
                    user_expiry = self.data.user_expiries[params.address].open_some(
                    )
                    sp.verify(sp.as_nat(sp.now - permit_submission_timestamp)
                              < user_expiry, "PERMIT_REVOKED")
                with sp.else_():
                    sp.verify(sp.as_nat(sp.now - permit_submission_timestamp)
                              < self.data.default_expiry, "PERMIT_REVOKED")
            self.data.permit_expiries[sp.pair(
                params.address, some_permit)] = sp.some(params.seconds)
            self.data.user_expiries[params.address] = sp.some(params.seconds)

    @sp.entry_point
    def approve(self, params):
        sp.set_type(params, sp.TRecord(spender=sp.TAddress,
                                       value=sp.TNat).layout(("spender", "value")))
        sp.verify(~self.data.paused)
        alreadyApproved = self.data.balances[sp.sender].approvals.get(
            params.spender, 0)
        sp.verify((alreadyApproved == 0) | (
            params.value == 0), "UnsafeAllowanceChange")
        self.data.balances[sp.sender].approvals[params.spender] = params.value

    @sp.entry_point
    def setPause(self, params):
        sp.set_type(params, sp.TBool)
        sp.verify(sp.sender == self.data.administrator)
        self.data.paused = params

    @sp.entry_point
    def setAdministrator(self, params):
        sp.set_type(params, sp.TAddress)
        sp.verify(sp.sender == self.data.administrator)
        self.data.administrator = params

    @sp.entry_point
    def mint(self, params):
        sp.set_type(params, sp.TRecord(address=sp.TAddress, value=sp.TNat))
        sp.verify(sp.sender == self.data.administrator)
        self.addAddressIfNecessary(params.address)
        self.data.balances[params.address].balance += params.value
        self.data.totalSupply += params.value

    @sp.entry_point
    def burn(self, params):
        sp.set_type(params, sp.TRecord(address=sp.TAddress, value=sp.TNat))
        sp.verify(sp.sender == self.data.administrator)
        sp.verify(self.data.balances[params.address].balance >= params.value)
        self.data.balances[params.address].balance = sp.as_nat(
            self.data.balances[params.address].balance - params.value)
        self.data.totalSupply = sp.as_nat(self.data.totalSupply - params.value)

    def addAddressIfNecessary(self, address):
        with sp.if_(~ self.data.balances.contains(address)):
            self.data.balances[address] = sp.record(balance=0, approvals={})

    @sp.entry_point
    def getBalance(self, params):
        sp.transfer(self.data.balances[params.arg.owner].balance, sp.tez(
            0), sp.contract(sp.TNat, params.target).open_some())

    @sp.entry_point
    def getAllowance(self, params):
        sp.transfer(self.data.balances[params.arg.owner].approvals[params.arg.spender], sp.tez(
            0), sp.contract(sp.TNat, params.target).open_some())

    @sp.entry_point
    def getTotalSupply(self, params):
        sp.transfer(self.data.totalSupply, sp.tez(
            0), sp.contract(sp.TNat, params.target).open_some())

    @sp.entry_point
    def getAdministrator(self, params):
        sp.transfer(self.data.administrator, sp.tez(
            0), sp.contract(sp.TAddress, params.target).open_some())

    @sp.entry_point
    def getCounter(self, params):
        sp.transfer(self.data.counter, sp.tez(0), sp.contract(
            sp.TNat, params.target).open_some())

    @sp.entry_point
    def getDefaultExpiry(self, params):
        sp.transfer(self.data.default_expiry, sp.tez(
            0), sp.contract(sp.TNat, params.target).open_some())


class Viewer(sp.Contract):
    def __init__(self, t):
        self.init(last=sp.none)
        self.init_type(sp.TRecord(last=sp.TOption(t)))

    @sp.entry_point
    def target(self, params):
        self.data.last = sp.some(params)


if "templates" not in __name__:
    @sp.add_test(name="FA12")
    def test():

        scenario = sp.test_scenario()
        scenario.h1("FA1.2 template - Fungible assets")

        scenario.table_of_contents()

        # sp.test_account generates ED25519 key-pairs deterministically:
        admin = sp.test_account("Administrator")
        alice = sp.test_account("Alice")
        bob = sp.test_account("Robert")
        carlos = sp.test_account("Carlos")

        # Let's display the accounts:
        scenario.h1("Accounts")
        scenario.show([admin, alice, bob, carlos])

        scenario.h1("Contract")
        c1 = FA12(admin.address)

        scenario.h1("Entry points")
        scenario += c1
        scenario.h2("Admin mints a few coins")
        scenario += c1.mint(address=alice.address, value=12).run(sender=admin)
        scenario += c1.mint(address=alice.address, value=3).run(sender=admin)
        scenario += c1.mint(address=alice.address, value=3).run(sender=admin)
        scenario.verify(c1.data.balances[alice.address].balance == 18)
        scenario.h2("Alice transfers to Bob")
        scenario += c1.transfer(from_=alice.address,
                                to_=bob.address, value=4).run(sender=alice)
        scenario.verify(c1.data.balances[alice.address].balance == 14)
        scenario.h2(
            "Bob tries to transfer from Alice but he doesn't have her approval")
        scenario += c1.transfer(from_=alice.address, to_=bob.address,
                                value=4).run(sender=bob, valid=False)
        scenario.h2("Alice approves Bob and Bob transfers")
        scenario += c1.approve(spender=bob.address, value=5).run(sender=alice)
        scenario += c1.transfer(from_=alice.address,
                                to_=bob.address, value=4).run(sender=bob)
        scenario.h2("Bob tries to over-transfer from Alice")
        scenario += c1.transfer(from_=alice.address, to_=bob.address,
                                value=4).run(sender=bob, valid=False)
        scenario.h2("Admin burns Bob token")
        scenario += c1.burn(address=bob.address, value=1).run(sender=admin)
        scenario.verify(c1.data.balances[alice.address].balance == 10)
        scenario.h2("Alice tries to burn Bob token")
        scenario += c1.burn(address=bob.address,
                            value=1).run(sender=alice, valid=False)
        scenario.h2(
            "Admin pauses the contract and Alice cannot transfer anymore")
        scenario += c1.setPause(True).run(sender=admin)
        scenario += c1.transfer(from_=alice.address, to_=bob.address,
                                value=4).run(sender=alice, valid=False)
        scenario.verify(c1.data.balances[alice.address].balance == 10)
        scenario.h2("Admin transfers while on pause")
        scenario += c1.transfer(from_=alice.address,
                                to_=bob.address, value=1).run(sender=admin)
        scenario.h2("Admin unpauses the contract and transfers are allowed")
        scenario += c1.setPause(False).run(sender=admin)
        scenario.verify(c1.data.balances[alice.address].balance == 9)
        scenario += c1.transfer(from_=alice.address,
                                to_=bob.address, value=1).run(sender=alice)

        scenario.verify(c1.data.totalSupply == 17)
        scenario.verify(c1.data.balances[alice.address].balance == 8)
        scenario.verify(c1.data.balances[bob.address].balance == 9)

        scenario.h2("Permit Submissions")
        scenario += c1.mint(address=carlos.address, value=10).run(sender=admin)
        scenario.verify(c1.data.balances[carlos.address].balance == 10)
        # add permit for transfer of 10 from carlos to bob. alice submits with correct info and also calls.
        params_bytes_1 = sp.pack(
            sp.pair(carlos.address, sp.pair(bob.address, 10)))
        params_bytes_2 = sp.pack(
            sp.pair(bob.address, sp.pair(carlos.address, 10)))
        params_hash_1 = sp.blake2b(params_bytes_1)
        params_hash_2 = sp.blake2b(params_bytes_2)
        unsigned_1 = sp.pack(sp.pair(sp.pair(sp.chain_id_cst(
            "0x9caecab9"), c1.address), sp.pair(0, params_hash_1)))
        signature_1 = sp.make_signature(
            secret_key=carlos.secret_key, message=unsigned_1, message_format="Raw")
        unsigned_2 = sp.pack(sp.pair(sp.pair(sp.chain_id_cst(
            "0x9caecab9"), c1.address), sp.pair(1, params_hash_2)))
        signature_2 = sp.make_signature(
            secret_key=bob.secret_key, message=unsigned_2, message_format="Raw")

        scenario += c1.permit([sp.pair(carlos.public_key, sp.pair(signature_1, params_hash_1)), sp.pair(bob.public_key, sp.pair(
            signature_2, params_hash_2))]).run(sender=alice, now=sp.timestamp(1571761674), chain_id=sp.chain_id_cst("0x9caecab9"))
        scenario.verify(c1.data.counter == 2)
        scenario.verify_equal(c1.data.permits[(
            sp.pair(carlos.address, params_hash_1))], sp.timestamp(1571761674))
        scenario.verify_equal(c1.data.permits[(
            sp.pair(bob.address, params_hash_2))], sp.timestamp(1571761674))

        scenario.h2("Execute transfer using permit")
        scenario += c1.transfer(from_=carlos.address, to_=bob.address,
                                value=10).run(sender=bob, now=sp.timestamp(1571761674))
        scenario.verify(c1.data.balances[carlos.address].balance == 0)
        scenario.verify(c1.data.balances[bob.address].balance == 19)
        # Permit deleted
        scenario.verify(~ c1.data.permits.contains(
            sp.pair(carlos.address, params_hash_1)))

        scenario += c1.transfer(from_=bob.address, to_=carlos.address,
                                value=10).run(sender=carlos, now=sp.timestamp(1571761674))
        scenario.verify(c1.data.balances[carlos.address].balance == 10)
        scenario.verify(c1.data.balances[bob.address].balance == 9)
        # Permit deleted
        scenario.verify(~ c1.data.permits.contains(
            sp.pair(bob.address, params_hash_2)))

        # Set Expiry to 0 and try to execute transfer at time less than submission_time + default_expiry, expect invalid transfer
        scenario.h2("Expired Permit")
        unsigned_3 = sp.pack(sp.pair(sp.pair(sp.chain_id_cst(
            "0x9caecab9"), c1.address), sp.pair(2, params_hash_1)))
        signature_3 = sp.make_signature(
            secret_key=carlos.secret_key, message=unsigned_3, message_format="Raw")
        scenario += c1.permit([sp.pair(carlos.public_key, sp.pair(signature_3, params_hash_1))]).run(
            sender=alice, now=sp.timestamp(1571761674), chain_id=sp.chain_id_cst("0x9caecab9"))
        scenario.verify(c1.data.counter == 3)
        scenario.verify_equal(c1.data.permits[(
            sp.pair(carlos.address, params_hash_1))], sp.timestamp(1571761674))
        scenario += c1.setExpiry(address=carlos.address, seconds=0, permit=sp.some(
            params_hash_1)).run(sender=carlos, now=sp.timestamp(1571761674))
        scenario.verify(c1.data.permit_expiries[sp.pair(
            carlos.address, params_hash_1)].open_some() == 0)
        scenario += c1.transfer(from_=carlos.address, to_=bob.address, value=10).run(
            sender=bob, now=sp.timestamp(1571761680), valid=False)  # Uses later time stamp

        scenario.h2("Delete Expired Permit")
        scenario += c1.delete_permits([sp.pair(carlos.address,
                                               params_hash_1)]).run(now=sp.timestamp(1571761680))
        scenario.verify(~ c1.data.permit_expiries.contains(
            sp.pair(carlos.address, params_hash_1)))
        scenario.verify(~ c1.data.permits.contains(
            sp.pair(carlos.address, params_hash_1)))

        scenario.h1("Views")
        scenario.h2("Balance")
        view_balance = Viewer(sp.TNat)
        scenario += view_balance
        scenario += c1.getBalance(arg=sp.record(owner=alice.address),
                                  target=view_balance.address)
        scenario.verify_equal(view_balance.data.last, sp.some(8))

        scenario.h2("Administrator")
        view_administrator = Viewer(sp.TAddress)
        scenario += view_administrator
        scenario += c1.getAdministrator(target=view_administrator.address)
        scenario.verify_equal(view_administrator.data.last,
                              sp.some(admin.address))

        scenario.h2("Total Supply")
        view_totalSupply = Viewer(sp.TNat)
        scenario += view_totalSupply
        scenario += c1.getTotalSupply(target=view_totalSupply.address)
        scenario.verify_equal(view_totalSupply.data.last, sp.some(27))

        scenario.h2("Allowance")
        view_allowance = Viewer(sp.TNat)
        scenario += view_allowance
        scenario += c1.getAllowance(arg=sp.record(owner=alice.address,
                                                  spender=bob.address), target=view_allowance.address)
        scenario.verify_equal(view_allowance.data.last, sp.some(1))

        scenario.h2("Counter")
        view_counter = Viewer(sp.TNat)
        scenario += view_counter
        scenario += c1.getCounter(target=view_counter.address)
        scenario.verify_equal(view_counter.data.last, sp.some(3))

        scenario.h2("Default Expiry")
        view_defaultExpiry = Viewer(sp.TNat)
        scenario += view_defaultExpiry
        scenario += c1.getDefaultExpiry(target=view_defaultExpiry.address)
        scenario.verify_equal(view_defaultExpiry.data.last, sp.some(50000))

