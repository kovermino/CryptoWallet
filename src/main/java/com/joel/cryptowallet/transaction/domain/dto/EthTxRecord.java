package com.joel.cryptowallet.transaction.domain.dto;

import com.joel.cryptowallet.transaction.domain.entity.EthTransactionEntity;
import com.joel.cryptowallet.transaction.domain.enums.TransactionType;
import lombok.Builder;

import java.math.BigInteger;

public record EthTxRecord(
        String txHash,
        TransactionType transactionType,
        String from,
        String to,
        BigInteger amount,
        BigInteger recordedBlockNode
) {
    @Builder
    public EthTxRecord {}

    public EthTransactionEntity toEthTransactionEntity() {
        return EthTransactionEntity.builder()
                .txHash(this.txHash)
                .transactionType(this.transactionType)
                .departure(this.from)
                .destination(this.to)
                .amount(this.amount)
                .recordedBlockNode(this.recordedBlockNode)
                .build();
    }
}
