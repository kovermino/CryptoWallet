package com.joel.cryptowallet.wallet.service;

import com.joel.cryptowallet.connector.EthereumConnector;
import com.joel.cryptowallet.user.UserEntity;
import com.joel.cryptowallet.user.UserRepository;
import com.joel.cryptowallet.user.UserStatus;
import com.joel.cryptowallet.user.dto.AccountDTO;
import com.joel.cryptowallet.wallet.controller.response.WalletCreationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockChainWalletService implements WalletService {

    private final UserRepository userRepository;
    private final EthereumConnector ethereumConnector;

    @Transactional
    @Override
    public WalletCreationResponse createWallet(String walletId, String password) {
        AccountDTO ethereumAccount = ethereumConnector.createAccount();
        UserEntity user = UserEntity.builder()
                .walletId(walletId)
                .password(password)
                .walletAddress(ethereumAccount.address())
                .privateKey(ethereumAccount.privateKey())
                .status(UserStatus.ACTIVATED)
                .build();
        userRepository.save(user);
        return WalletCreationResponse.builder()
                .id(walletId)
                .address(ethereumAccount.address())
                .privateKey(ethereumAccount.privateKey())
                .build();
    }
}
