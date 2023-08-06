package com.joel.cryptowallet.transaction.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Map;

@Builder
@Getter
public class EthTxTotal {
    private BigInteger lastCheckedNode;
    private Map<String, EthTxPerAddress> transactionsWithAddress;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EthTxTotal txTotal = (EthTxTotal) o;

        if (!this.lastCheckedNode.equals(txTotal.lastCheckedNode))
            return false;
        return this.transactionsWithAddress.equals(txTotal.transactionsWithAddress);
    }

    @Override
    public int hashCode() {
        int result = lastCheckedNode != null ? lastCheckedNode.hashCode() : 0;
        result = 31 * result + (transactionsWithAddress != null ? transactionsWithAddress.hashCode() : 0);
        return result;
    }
}
