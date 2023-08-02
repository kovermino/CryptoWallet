package com.joel.cryptowallet.wallet.service;

import com.joel.cryptowallet.connector.EthereumConnector;
import com.joel.cryptowallet.wallet.domain.entity.WalletUserEntity;
import com.joel.cryptowallet.wallet.repository.WalletUserRepository;
import com.joel.cryptowallet.wallet.domain.enums.WalletUserStatus;
import com.joel.cryptowallet.wallet.domain.dto.AccountDTO;
import com.joel.cryptowallet.wallet.controller.response.WalletCreationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockChainWalletService implements WalletService {

    private final WalletUserRepository walletUserRepository;
    private final EthereumConnector ethereumConnector;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public WalletCreationResponse createWallet(String walletId, String password) {
        AccountDTO ethereumAccount = ethereumConnector.createAccount();
        WalletUserEntity user = WalletUserEntity.builder()
                .walletId(walletId)
                .password(passwordEncoder.encode(password))
                .walletAddress(ethereumAccount.address())
                .privateKey(passwordEncoder.encode(ethereumAccount.privateKey()))
                .status(WalletUserStatus.ACTIVATED)
                .build();
        walletUserRepository.save(user);
        return WalletCreationResponse.builder()
                .id(walletId)
                .address(ethereumAccount.address())
                .privateKey(ethereumAccount.privateKey())
                .build();
    }
}
