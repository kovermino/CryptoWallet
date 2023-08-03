package com.joel.cryptowallet.transaction.domain.entity;

import com.joel.cryptowallet.transaction.domain.enums.TransactionType;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class EthTransactionEntityId implements Serializable {
    private String txHash;
    private TransactionType transactionType;
}
