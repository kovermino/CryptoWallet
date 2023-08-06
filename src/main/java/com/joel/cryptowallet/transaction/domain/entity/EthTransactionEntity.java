package com.joel.cryptowallet.transaction.domain.entity;

import com.joel.cryptowallet.transaction.domain.enums.TransactionStatus;
import com.joel.cryptowallet.transaction.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity(name = "ETH_TX")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(EthTransactionEntityId.class)
public class EthTransactionEntity {
    @Id
    private String txHash;

    @Id
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private String departure;
    private String destination;
    private BigInteger amount;
    private BigInteger recordedBlockNode;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
