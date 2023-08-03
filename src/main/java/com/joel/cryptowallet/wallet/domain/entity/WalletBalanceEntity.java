package com.joel.cryptowallet.wallet.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletBalanceEntity {
    @Id
    private String walletId;
    private BigInteger balance;
    private BigInteger lastCheckedNode;
    private BigInteger balanceFromBlockChain;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public static WalletBalanceEntity getInitialBalance(String walletId) {
        return WalletBalanceEntity.builder()
                .walletId(walletId)
                .balance(BigInteger.ZERO)
                .build();
    }
}
