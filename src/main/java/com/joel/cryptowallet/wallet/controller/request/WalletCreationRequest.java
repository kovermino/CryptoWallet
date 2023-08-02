package com.joel.cryptowallet.wallet.controller.request;

import lombok.Builder;

public record WalletCreationRequest(
        String id,
        String password
) {
    @Builder
    public WalletCreationRequest {}
}