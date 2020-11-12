[{"shortname": "FA12", "longname": "FA12", "scenario": [{"action": "html", "tag": "h1", "inner": "FA1.2 template - Fungible assets", "line_no": 114}, {"action": "html", "tag": "p", "inner": "[[TABLEOFCONTENTS]]", "line_no": 116}, {"action": "html", "tag": "h1", "inner": "Accounts", "line_no": 124}, {"action": "show", "html": true, "stripStrings": false, "expression": "(list 125 (account_of_seed \"Administrator\" 119) (account_of_seed \"Alice\" 120) (account_of_seed \"Robert\" 121))", "line_no": 125}, {"action": "html", "tag": "h1", "inner": "Contract", "line_no": 127}, {"action": "html", "tag": "h1", "inner": "Entry points", "line_no": 130}, {"action": "newContract", "id": 0, "export": "(storage (record 6 (administrator (reduce (attr (account_of_seed \"Administrator\" 119) \"address\" 119) 119)) (balances (type_annotation (big_map 6 ) (bigmap (unknown 9) (record ((approvals (map \"address\" \"nat\")) (balance \"nat\") (counter \"nat\")) None)) 6)) (paused (literal (bool False) 6)) (totalSupply (literal (intOrNat 0) 6)))\nstorage_type (())\nmessages ((approve True ((set_type (params 46) (record ((spender \"address\") (value \"nat\")) (Some ((\"spender\") (\"value\")))) 47) (verify (invert (attr (data) \"paused\" 11) 48) False 48) (verify (or (eq (getItemDefault (attr (getItem (attr (data) \"balances\" 13) (sender) 49) \"approvals\" 49) (attr (params 46) \"spender\" 49) (literal (intOrNat 0) 49) 49) (literal (intOrNat 0) 50) 50) (eq (attr (params 46) \"value\" 50) (literal (intOrNat 0) 50) 50) 50) False (literal (string \"UnsafeAllowanceChange\") 50) 50) (set (getItem (attr (getItem (attr (data) \"balances\" 13) (sender) 51) \"approvals\" 51) (attr (params 46) \"spender\" 49) 51) (attr (params 46) \"value\" 50) 51))) (burn True ((set_type (params 74) (record ((address \"address\") (value \"nat\")) None) 75) (verify (eq (sender) (attr (data) \"administrator\" 10) 76) False 76) (verify (ge (attr (getItem (attr (data) \"balances\" 13) (attr (params 74) \"address\" 77) 77) \"balance\" 77) (attr (params 74) \"value\" 77) 77) False 77) (set (attr (getItem (attr (data) \"balances\" 13) (attr (params 74) \"address\" 77) 78) \"balance\" 78) (openVariant (isNat (sub (attr (getItem (attr (data) \"balances\" 13) (attr (params 74) \"address\" 77) 78) \"balance\" 78) (attr (params 74) \"value\" 77) 78) 78) \"Some\" \"None\" 78) 78) (set (attr (data) \"totalSupply\" 79) (openVariant (isNat (sub (attr (data) \"totalSupply\" 79) (attr (params 74) \"value\" 77) 79) 79) \"Some\" \"None\" 79) 79))) (getAdministrator True ((set (operations 99) (cons (transfer (attr (data) \"administrator\" 10) (literal (mutez 0) 99) (openVariant (contract \"\" \"address\" (attr (params 98) \"target\" 99) 99) \"Some\" \"None\" 99) 99) (operations 99) 99) 99))) (getAllowance True ((set (operations 91) (cons (transfer (getItem (attr (getItem (attr (data) \"balances\" 13) (attr (attr (params 90) \"arg\" 91) \"owner\" 91) 91) \"approvals\" 91) (attr (attr (params 90) \"arg\" 91) \"spender\" 91) 91) (literal (mutez 0) 91) (openVariant (contract \"\" \"nat\" (attr (params 90) \"target\" 91) 91) \"Some\" \"None\" 91) 91) (operations 91) 91) 91))) (getBalance True ((set (operations 87) (cons (transfer (attr (getItem (attr (data) \"balances\" 13) (attr (attr (params 86) \"arg\" 87) \"owner\" 87) 87) \"balance\" 87) (literal (mutez 0) 87) (openVariant (contract \"\" \"nat\" (attr (params 86) \"target\" 87) 87) \"Some\" \"None\" 87) 87) (operations 87) 87) 87))) (getTotalSupply True ((set (operations 95) (cons (transfer (attr (data) \"totalSupply\" 79) (literal (mutez 0) 95) (openVariant (contract \"\" \"nat\" (attr (params 94) \"target\" 95) 95) \"Some\" \"None\" 95) 95) (operations 95) 95) 95))) (mint True ((set_type (params 66) (record ((address \"address\") (value \"nat\")) None) 67) (verify (eq (sender) (attr (data) \"administrator\" 10) 68) False 68) (ifBlock (invert (contains (attr (data) \"balances\" 13) (attr (params 66) \"address\" 69) 82) 82) ((set (getItem (attr (data) \"balances\" 13) (attr (params 66) \"address\" 69) 83) (record 83 (approvals (map 83 )) (balance (literal (intOrNat 0) 83)) (counter (literal (intOrNat 0) 83))) 83)) 82) (set (attr (getItem (attr (data) \"balances\" 13) (attr (params 66) \"address\" 69) 70) \"balance\" 70) (add (attr (getItem (attr (data) \"balances\" 13) (attr (params 66) \"address\" 69) 70) \"balance\" 70) (attr (params 66) \"value\" 70) 70) 70) (set (attr (data) \"totalSupply\" 79) (add (attr (data) \"totalSupply\" 79) (attr (params 66) \"value\" 70) 71) 71))) (setAdministrator True ((set_type (params 60) \"address\" 61) (verify (eq (sender) (attr (data) \"administrator\" 10) 62) False 62) (set (attr (data) \"administrator\" 10) (params 60) 63))) (setPause True ((set_type (params 54) \"bool\" 55) (verify (eq (sender) (attr (data) \"administrator\" 10) 56) False 56) (set (attr (data) \"paused\" 11) (params 54) 57))) (transfer True ((set_type (params 22) (record ((from_ \"address\") (to_ \"address\") (value \"nat\")) None) 23) (defineLocal \"y5\" (call_lambda (global \"transfer_helper\" 131) (record 25 (in_param (record 24 (from_ (attr (params 22) \"from_\" 24)) (sender_ (sender)) (to_ (attr (params 22) \"to_\" 24)) (value (attr (params 22) \"value\" 24)))) (in_storage (data))) 25) 25) (set (data) (attr (getLocal \"y5\" 25) \"storage\" 25) 25) (forGroup \"op\" (attr (getLocal \"y5\" 25) \"operations\" 25) ((set (operations 25) (cons (iter \"op\" 25) (operations 25) 25) 25)) 25))) (transferSigned True ((set_type (params 28) (record ((from_ \"address\") (pk \"key\") (signed \"signature\") (to_ \"address\") (value \"nat\")) None) 29) (defineLocal \"sender\" (sender) 30) (ifBlock (neq (attr (params 28) \"from_\" 31) (getLocal \"sender\" 31) 31) ((ifBlock (check_signature (attr (params 28) \"pk\" 37) (attr (params 28) \"signed\" 38) (hashCrypto \"BLAKE2B\" (call_michelson (op \"SELF; ADDRESS; CHAIN_ID; PAIR; PAIR; PACK\" (tuple \"nat\" \"bytes\") \"out\" \"bytes\") 36 (tuple 36 (attr (getItem (attr (data) \"balances\" 13) (attr (params 28) \"from_\" 31) 32) \"counter\" 33) (hashCrypto \"BLAKE2B\" (concat (list 34 (pack (attr (params 28) \"from_\" 31) 34) (pack (attr (params 28) \"to_\" 34) 34) (pack (attr (params 28) \"value\" 34) 34)) 34) 35))) 36) 38) ((set (getLocal \"sender\" 131) (to_address (implicit_account (hash_key (attr (params 28) \"pk\" 37) 37) 37) 37) 39)) 38) (elseBlock ((failwith (tuple 41 (literal (string \"MISSIGNED\") 131) (hashCrypto \"BLAKE2B\" (call_michelson (op \"SELF; ADDRESS; CHAIN_ID; PAIR; PAIR; PACK\" (tuple \"nat\" \"bytes\") \"out\" \"bytes\") 36 (tuple 36 (attr (getItem (attr (data) \"balances\" 13) (attr (params 28) \"from_\" 31) 32) \"counter\" 33) (hashCrypto \"BLAKE2B\" (concat (list 34 (pack (attr (params 28) \"from_\" 31) 34) (pack (attr (params 28) \"to_\" 34) 34) (pack (attr (params 28) \"value\" 34) 34)) 34) 35))) 36)) 41)))) 31) (defineLocal \"y6\" (call_lambda (global \"transfer_helper\" 131) (record 43 (in_param (record 43 (from_ (attr (params 28) \"from_\" 31)) (sender_ (getLocal \"sender\" 43)) (to_ (attr (params 28) \"to_\" 34)) (value (attr (params 28) \"value\" 34)))) (in_storage (data))) 43) 43) (set (data) (attr (getLocal \"y6\" 43) \"storage\" 43) 43) (forGroup \"op\" (attr (getLocal \"y6\" 43) \"operations\" 43) ((set (operations 43) (cons (iter \"op\" 43) (operations 43) 43) 43)) 43))))\nflags ()\nglobals ((transfer_helper (lambda 0 \"\" 131 ((defineLocal \"__operations__\" (type_annotation (list 131 ) (list \"operation\") 131) 131) (defineLocal \"__storage__\" (type_annotation (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_storage\" 131) (unknown 11) 131) 131) (seq \"__s4\" ((verify (or (eq (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"sender_\" 10) (attr (data) \"administrator\" 10) 10) (and (invert (attr (data) \"paused\" 11) 11) (or (eq (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"from_\" 12) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"sender_\" 10) 12) (ge (getItem (attr (getItem (attr (data) \"balances\" 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"from_\" 12) 13) \"approvals\" 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"sender_\" 10) 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"value\" 13) 13) 12) 11) 10) False 10) (ifBlock (invert (contains (attr (data) \"balances\" 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"to_\" 14) 82) 82) ((set (getItem (attr (data) \"balances\" 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"to_\" 14) 83) (record 83 (approvals (map 83 )) (balance (literal (intOrNat 0) 83)) (counter (literal (intOrNat 0) 83))) 83)) 82) (verify (ge (attr (getItem (attr (data) \"balances\" 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"from_\" 12) 15) \"balance\" 15) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"value\" 13) 15) False 15) (set (attr (getItem (attr (data) \"balances\" 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"from_\" 12) 16) \"balance\" 16) (openVariant (isNat (sub (attr (getItem (attr (data) \"balances\" 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"from_\" 12) 16) \"balance\" 16) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"value\" 13) 16) 16) \"Some\" \"None\" 16) 16) (set (attr (getItem (attr (data) \"balances\" 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"to_\" 14) 17) \"balance\" 17) (add (attr (getItem (attr (data) \"balances\" 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"to_\" 14) 17) \"balance\" 17) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"value\" 13) 17) 17) (ifBlock (and (neq (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"from_\" 12) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"sender_\" 10) 18) (neq (attr (data) \"administrator\" 10) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"sender_\" 10) 18) 18) ((set (getItem (attr (getItem (attr (data) \"balances\" 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"from_\" 12) 19) \"approvals\" 19) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"sender_\" 10) 19) (openVariant (isNat (sub (getItem (attr (getItem (attr (data) \"balances\" 13) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"from_\" 12) 19) \"approvals\" 19) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"sender_\" 10) 19) (attr (attr (lambdaParams 0 \"\" 131 (unknown 10)) \"in_param\" 131) \"value\" 13) 19) 19) \"Some\" \"None\" 19) 19)) 18)) 131) (bind \"__s4\" ((result (record 131 (operations (getLocal \"__operations__\" 131)) (result (getLocal \"__s4\" 131)) (storage (getLocal \"__storage__\" 131))) 131)) 131)))))\nentry_points_layout ()\nbalance ())", "line_no": 131, "show": true, "accept_unknown_types": false}, {"action": "html", "tag": "h2", "inner": "Admin mints a few coins", "line_no": 132}, {"action": "message", "id": 0, "message": "mint", "params": "(record 133 (address (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)) (value (literal (intOrNat 12) 133)))", "line_no": 133, "title": "", "messageClass": "", "source": "none", "sender": "seed:Administrator", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 133)", "show": true, "valid": "(literal (bool True) 133)"}, {"action": "message", "id": 0, "message": "mint", "params": "(record 134 (address (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)) (value (literal (intOrNat 3) 134)))", "line_no": 134, "title": "", "messageClass": "", "source": "none", "sender": "seed:Administrator", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 134)", "show": true, "valid": "(literal (bool True) 134)"}, {"action": "message", "id": 0, "message": "mint", "params": "(record 135 (address (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)) (value (literal (intOrNat 3) 135)))", "line_no": 135, "title": "", "messageClass": "", "source": "none", "sender": "seed:Administrator", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 135)", "show": true, "valid": "(literal (bool True) 135)"}, {"action": "verify", "condition": "(eq (attr (getItem (attr (contractData 0 135) \"balances\" 136) (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120) 136) \"balance\" 136) (literal (intOrNat 18) 136) 136)", "line_no": 136}, {"action": "html", "tag": "h2", "inner": "Alice transfers to Bob", "line_no": 137}, {"action": "message", "id": 0, "message": "transfer", "params": "(record 138 (from_ (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)) (to_ (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121)) (value (literal (intOrNat 4) 138)))", "line_no": 138, "title": "", "messageClass": "", "source": "none", "sender": "seed:Alice", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 138)", "show": true, "valid": "(literal (bool True) 138)"}, {"action": "verify", "condition": "(eq (attr (getItem (attr (contractData 0 138) \"balances\" 139) (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120) 139) \"balance\" 139) (literal (intOrNat 14) 139) 139)", "line_no": 139}, {"action": "html", "tag": "h2", "inner": "Bob tries to transfer from Alice but he doesn't have her approval", "line_no": 140}, {"action": "message", "id": 0, "message": "transfer", "params": "(record 141 (from_ (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)) (to_ (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121)) (value (literal (intOrNat 4) 141)))", "line_no": 141, "title": "", "messageClass": "", "source": "none", "sender": "seed:Robert", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 141)", "show": true, "valid": "(literal (bool False) 141)"}, {"action": "html", "tag": "h2", "inner": "Alice approves Bob and Bob transfers", "line_no": 142}, {"action": "message", "id": 0, "message": "approve", "params": "(record 143 (spender (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121)) (value (literal (intOrNat 5) 143)))", "line_no": 143, "title": "", "messageClass": "", "source": "none", "sender": "seed:Alice", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 143)", "show": true, "valid": "(literal (bool True) 143)"}, {"action": "message", "id": 0, "message": "transfer", "params": "(record 144 (from_ (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)) (to_ (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121)) (value (literal (intOrNat 4) 144)))", "line_no": 144, "title": "", "messageClass": "", "source": "none", "sender": "seed:Robert", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 144)", "show": true, "valid": "(literal (bool True) 144)"}, {"action": "html", "tag": "h2", "inner": "Bob tries to over-transfer from Alice", "line_no": 145}, {"action": "message", "id": 0, "message": "transfer", "params": "(record 146 (from_ (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)) (to_ (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121)) (value (literal (intOrNat 4) 146)))", "line_no": 146, "title": "", "messageClass": "", "source": "none", "sender": "seed:Robert", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 146)", "show": true, "valid": "(literal (bool False) 146)"}, {"action": "html", "tag": "h2", "inner": "Admin burns Bob token", "line_no": 147}, {"action": "message", "id": 0, "message": "burn", "params": "(record 148 (address (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121)) (value (literal (intOrNat 1) 148)))", "line_no": 148, "title": "", "messageClass": "", "source": "none", "sender": "seed:Administrator", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 148)", "show": true, "valid": "(literal (bool True) 148)"}, {"action": "verify", "condition": "(eq (attr (getItem (attr (contractData 0 148) \"balances\" 149) (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120) 149) \"balance\" 149) (literal (intOrNat 10) 149) 149)", "line_no": 149}, {"action": "html", "tag": "h2", "inner": "Alice tries to burn Bob token", "line_no": 150}, {"action": "message", "id": 0, "message": "burn", "params": "(record 151 (address (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121)) (value (literal (intOrNat 1) 151)))", "line_no": 151, "title": "", "messageClass": "", "source": "none", "sender": "seed:Alice", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 151)", "show": true, "valid": "(literal (bool False) 151)"}, {"action": "html", "tag": "h2", "inner": "Admin pauses the contract and Alice cannot transfer anymore", "line_no": 152}, {"action": "message", "id": 0, "message": "setPause", "params": "(literal (bool True) 153)", "line_no": 153, "title": "", "messageClass": "", "source": "none", "sender": "seed:Administrator", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 153)", "show": true, "valid": "(literal (bool True) 153)"}, {"action": "message", "id": 0, "message": "transfer", "params": "(record 154 (from_ (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)) (to_ (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121)) (value (literal (intOrNat 4) 154)))", "line_no": 154, "title": "", "messageClass": "", "source": "none", "sender": "seed:Alice", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 154)", "show": true, "valid": "(literal (bool False) 154)"}, {"action": "verify", "condition": "(eq (attr (getItem (attr (contractData 0 154) \"balances\" 155) (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120) 155) \"balance\" 155) (literal (intOrNat 10) 155) 155)", "line_no": 155}, {"action": "html", "tag": "h2", "inner": "Admin transfers while on pause", "line_no": 156}, {"action": "message", "id": 0, "message": "transfer", "params": "(record 157 (from_ (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)) (to_ (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121)) (value (literal (intOrNat 1) 157)))", "line_no": 157, "title": "", "messageClass": "", "source": "none", "sender": "seed:Administrator", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 157)", "show": true, "valid": "(literal (bool True) 157)"}, {"action": "html", "tag": "h2", "inner": "Admin unpauses the contract and transferts are allowed", "line_no": 158}, {"action": "message", "id": 0, "message": "setPause", "params": "(literal (bool False) 159)", "line_no": 159, "title": "", "messageClass": "", "source": "none", "sender": "seed:Administrator", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 159)", "show": true, "valid": "(literal (bool True) 159)"}, {"action": "verify", "condition": "(eq (attr (getItem (attr (contractData 0 159) \"balances\" 160) (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120) 160) \"balance\" 160) (literal (intOrNat 9) 160) 160)", "line_no": 160}, {"action": "message", "id": 0, "message": "transfer", "params": "(record 161 (from_ (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)) (to_ (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121)) (value (literal (intOrNat 1) 161)))", "line_no": 161, "title": "", "messageClass": "", "source": "none", "sender": "seed:Alice", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 161)", "show": true, "valid": "(literal (bool True) 161)"}, {"action": "verify", "condition": "(eq (attr (contractData 0 161) \"totalSupply\" 163) (literal (intOrNat 17) 163) 163)", "line_no": 163}, {"action": "verify", "condition": "(eq (attr (getItem (attr (contractData 0 161) \"balances\" 164) (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120) 164) \"balance\" 164) (literal (intOrNat 8) 164) 164)", "line_no": 164}, {"action": "verify", "condition": "(eq (attr (getItem (attr (contractData 0 161) \"balances\" 164) (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121) 165) \"balance\" 165) (literal (intOrNat 9) 165) 165)", "line_no": 165}, {"action": "html", "tag": "h1", "inner": "Views", "line_no": 167}, {"action": "html", "tag": "h2", "inner": "Balance", "line_no": 168}, {"action": "newContract", "id": 2, "export": "(storage (record 103 (last (variant \"None\" (unit) -1)))\nstorage_type ((record ((last (option \"nat\"))) None))\nmessages ((target True ((set (attr (data) \"last\" 107) (variant \"Some\" (params 106) 107) 107))))\nflags ()\nglobals ()\nentry_points_layout ()\nbalance ())", "line_no": 170, "show": true, "accept_unknown_types": false}, {"action": "message", "id": 0, "message": "getBalance", "params": "(record 171 (arg (record 171 (owner (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)))) (target (literal (local-address 2) 104)))", "line_no": 171, "title": "", "messageClass": "", "source": "none", "sender": "none", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 171)", "show": true, "valid": "(literal (bool True) 171)"}, {"action": "verify", "condition": "(eq (pack (type_annotation (attr (contractData 2 170) \"last\" 172) (unknown 17) 172) 172) (pack (type_annotation (variant \"Some\" (literal (intOrNat 8) 172) 172) (unknown 17) 172) 172) 172)", "line_no": 172}, {"action": "html", "tag": "h2", "inner": "Administrator", "line_no": 174}, {"action": "newContract", "id": 4, "export": "(storage (record 103 (last (variant \"None\" (unit) -1)))\nstorage_type ((record ((last (option \"address\"))) None))\nmessages ((target True ((set (attr (data) \"last\" 107) (variant \"Some\" (params 106) 107) 107))))\nflags ()\nglobals ()\nentry_points_layout ()\nbalance ())", "line_no": 176, "show": true, "accept_unknown_types": false}, {"action": "message", "id": 0, "message": "getAdministrator", "params": "(record 177 (target (literal (local-address 4) 104)))", "line_no": 177, "title": "", "messageClass": "", "source": "none", "sender": "none", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 177)", "show": true, "valid": "(literal (bool True) 177)"}, {"action": "verify", "condition": "(eq (pack (type_annotation (attr (contractData 4 176) \"last\" 178) (unknown 18) 178) 178) (pack (type_annotation (variant \"Some\" (reduce (attr (account_of_seed \"Administrator\" 119) \"address\" 119) 119) 178) (unknown 18) 178) 178) 178)", "line_no": 178}, {"action": "html", "tag": "h2", "inner": "Total Supply", "line_no": 180}, {"action": "newContract", "id": 6, "export": "(storage (record 103 (last (variant \"None\" (unit) -1)))\nstorage_type ((record ((last (option \"nat\"))) None))\nmessages ((target True ((set (attr (data) \"last\" 107) (variant \"Some\" (params 106) 107) 107))))\nflags ()\nglobals ()\nentry_points_layout ()\nbalance ())", "line_no": 182, "show": true, "accept_unknown_types": false}, {"action": "message", "id": 0, "message": "getTotalSupply", "params": "(record 183 (target (literal (local-address 6) 104)))", "line_no": 183, "title": "", "messageClass": "", "source": "none", "sender": "none", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 183)", "show": true, "valid": "(literal (bool True) 183)"}, {"action": "verify", "condition": "(eq (pack (type_annotation (attr (contractData 6 182) \"last\" 184) (unknown 19) 184) 184) (pack (type_annotation (variant \"Some\" (literal (intOrNat 17) 184) 184) (unknown 19) 184) 184) 184)", "line_no": 184}, {"action": "html", "tag": "h2", "inner": "Allowance", "line_no": 186}, {"action": "newContract", "id": 8, "export": "(storage (record 103 (last (variant \"None\" (unit) -1)))\nstorage_type ((record ((last (option \"nat\"))) None))\nmessages ((target True ((set (attr (data) \"last\" 107) (variant \"Some\" (params 106) 107) 107))))\nflags ()\nglobals ()\nentry_points_layout ()\nbalance ())", "line_no": 188, "show": true, "accept_unknown_types": false}, {"action": "message", "id": 0, "message": "getAllowance", "params": "(record 189 (arg (record 189 (owner (reduce (attr (account_of_seed \"Alice\" 120) \"address\" 120) 120)) (spender (reduce (attr (account_of_seed \"Robert\" 121) \"address\" 121) 121)))) (target (literal (local-address 8) 104)))", "line_no": 189, "title": "", "messageClass": "", "source": "none", "sender": "none", "chain_id": "", "time": "(literal (timestamp 0) 6)", "amount": "(literal (mutez 0) 1)", "level": "(literal (intOrNat 0) 189)", "show": true, "valid": "(literal (bool True) 189)"}, {"action": "verify", "condition": "(eq (pack (type_annotation (attr (contractData 8 188) \"last\" 190) (unknown 20) 190) 190) (pack (type_annotation (variant \"Some\" (literal (intOrNat 1) 190) 190) (unknown 20) 190) 190) 190)", "line_no": 190}]}]