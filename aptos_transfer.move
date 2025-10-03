address owner {
    module aptos_transfer {
        use 0x1::coin;
        use 0x1::aptos_coin;
        use std::signer;

        /// Transfer APT coin from the sender to the recipient.
        /// Amount is in Octas (1 APT = 100,000,000 Octas).
        public entry fun transfer_aptos(sender: &signer, recipient: address, amount: u64) acquires coin::CoinStore {
            coin::transfer<aptos_coin::AptosCoin>(sender, recipient, amount);
        }
    }
}
