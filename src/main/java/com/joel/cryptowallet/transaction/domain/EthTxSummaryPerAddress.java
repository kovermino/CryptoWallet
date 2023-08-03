package com.joel.cryptowallet.transaction.domain;

import com.joel.cryptowallet.transaction.domain.dto.EthTxRecord;
import com.joel.cryptowallet.transaction.domain.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Builder
@Getter
public class EthTxSummaryPerAddress {
    private String address;
    private BigInteger balance;
    private List<EthTxRecord> transactionList;

    public BigInteger totalTxAmount(BigInteger currentNode) {
        var totalAmount = transactionList.stream()
                .filter(transaction -> transaction.recordedBlockNode().longValue() + 12 <= currentNode.longValue())
                .mapToLong(transaction -> {
                    if(transaction.transactionType() == TransactionType.DEPOSIT) {
                        return transaction.amount().longValue();
                    } else {
                        return transaction.amount().longValue() * -1;
                    }
                })
                .sum();
        return BigInteger.valueOf(totalAmount);
    }
}
