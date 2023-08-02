package com.joel.cryptowallet.wallet.controller.response;

import lombok.Builder;

public record WalletCreationResponse(
        String id,
        String address,
        String privateKey
) {
    @Builder
    public WalletCreationResponse {}
}
