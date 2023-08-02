package com.joel.cryptowallet.user.dto;

import lombok.Builder;

public record AccountDTO(
        String address,
        String privateKey
) {
    @Builder
    public AccountDTO {}
}
