package com.joel.cryptowallet.wallet.service;

import com.joel.cryptowallet.wallet.controller.response.WalletCreationResponse;

public interface WalletService {
    WalletCreationResponse createWallet(String id, String password);
}
