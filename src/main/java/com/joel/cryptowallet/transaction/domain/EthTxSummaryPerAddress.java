package com.joel.cryptowallet.transaction.domain;

import com.joel.cryptowallet.transaction.domain.dto.EthTxRecord;
import com.joel.cryptowallet.transaction.domain.enums.TransactionType;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class EthTxSummaryPerAddress {
    private String address;
    private List<EthTxRecord> transactionList;

    public EthTxSummaryPerAddress(String address) {
        this.address = address;
        transactionList = new LinkedList<>();
    }

    public String getAddress() {
        return address;
    }

    public List<EthTxRecord> getTransactionList() {
        return transactionList;
    }

    public void addTransaction(EthTxRecord tx) {
        this.transactionList.add(tx);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EthTxSummaryPerAddress that = (EthTxSummaryPerAddress) o;

        if (!this.address.equals(that.address)) return false;
        return transactionList.equals(that.transactionList);
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (transactionList != null ? transactionList.hashCode() : 0);
        return result;
    }
}
