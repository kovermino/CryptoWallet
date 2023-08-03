package com.joel.cryptowallet.wallet.domain.dto;

import lombok.Builder;

import java.math.BigInteger;

public record AccountDTO(
        String address,
        String privateKey,
        BigInteger lastCheckedNode
) {
    @Builder
    public AccountDTO {}
}
