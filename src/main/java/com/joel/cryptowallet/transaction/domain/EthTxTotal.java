package com.joel.cryptowallet.transaction.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Map;

@Builder
@Getter
public class EthTxTotal {
    private BigInteger lastCheckedNode;
    private Map<String, EthTxSummaryPerAddress> transactionsWithAddress;
}
