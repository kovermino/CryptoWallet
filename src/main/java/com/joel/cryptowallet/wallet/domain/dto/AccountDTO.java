package com.joel.cryptowallet.wallet.domain.dto;

import lombok.Builder;

public record AccountDTO(
        String address,
        String privateKey
) {
    @Builder
    public AccountDTO {}
}
