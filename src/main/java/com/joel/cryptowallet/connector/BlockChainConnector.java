package com.joel.cryptowallet.connector;

import com.joel.cryptowallet.wallet.domain.dto.AccountDTO;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface BlockChainConnector {
    AccountDTO createAccount() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
}
