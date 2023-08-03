package com.joel.cryptowallet.wallet.service;

import com.joel.cryptowallet.wallet.controller.WalletBalanceResponse;
import com.joel.cryptowallet.wallet.controller.response.WalletCreationResponse;

public interface WalletService {
    WalletCreationResponse createWallet(String walletId, String password);
    WalletBalanceResponse getBalance(String walletAddress);
}
