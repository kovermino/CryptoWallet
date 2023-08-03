package com.joel.cryptowallet.connector;

import com.joel.cryptowallet.transaction.domain.EthTxTotal;
import com.joel.cryptowallet.wallet.domain.dto.AccountDTO;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface BlockChainConnector {
    AccountDTO createAccount() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
    EthTxTotal retrieveTransactions(BigInteger startBlockNodeNumber);
}
