package com.joel.cryptowallet.transaction.domain;

import com.joel.cryptowallet.transaction.domain.entity.EthTransactionEntity;
import com.joel.cryptowallet.transaction.domain.enums.TransactionStatus;
import com.joel.cryptowallet.transaction.domain.enums.TransactionType;
import lombok.Builder;

import java.math.BigInteger;

public record EthTxRecord(
        String txHash,
        TransactionType transactionType,
        TransactionStatus transactionStatus,
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
                .transactionStatus(this.transactionStatus)
                .departure(this.from)
                .destination(this.to)
                .amount(this.amount)
                .recordedBlockNode(this.recordedBlockNode)
                .build();
    }
}
