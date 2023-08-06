package com.joel.cryptowallet.wallet.controller.response;

import lombok.Builder;

public record WalletBalanceResponse(
        String address,
        String balance
) {
    @Builder
    public WalletBalanceResponse {}
}
