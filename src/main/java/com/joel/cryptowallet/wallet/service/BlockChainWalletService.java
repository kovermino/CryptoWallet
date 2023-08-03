package com.joel.cryptowallet.wallet.service;

import com.joel.cryptowallet.connector.EthereumConnector;
import com.joel.cryptowallet.wallet.controller.WalletBalanceResponse;
import com.joel.cryptowallet.wallet.domain.entity.WalletBalanceEntity;
import com.joel.cryptowallet.wallet.domain.entity.WalletUserEntity;
import com.joel.cryptowallet.wallet.repository.WalletBalanceRepository;
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
    private final WalletBalanceRepository walletBalanceRepository;
    private final EthereumConnector ethereumConnector;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public WalletCreationResponse createWallet(String walletId, String password) {
        AccountDTO ethereumAccount = ethereumConnector.createAccount();

        if(walletUserRepository.existsById(walletId)) {
            throw new RuntimeException("해당 아이디의 지갑이 이미 존재합니다");
        }

        WalletUserEntity user = WalletUserEntity.builder()
                .walletId(walletId)
                .password(passwordEncoder.encode(password))
                .walletAddress(ethereumAccount.address())
                .privateKey(passwordEncoder.encode(ethereumAccount.privateKey()))
                .status(WalletUserStatus.ACTIVATED)
                .build();
        walletUserRepository.save(user);

        WalletBalanceEntity initialBalance = WalletBalanceEntity.getInitialBalance(walletId, ethereumAccount.address());
        walletBalanceRepository.save(initialBalance);

        return WalletCreationResponse.builder()
                .id(walletId)
                .address(ethereumAccount.address())
                .privateKey(ethereumAccount.privateKey())
                .build();
    }

    @Override
    public WalletBalanceResponse getBalance(String walletAddress) {
        var walletBalanceOptional = walletBalanceRepository.findByAddress(walletAddress);
        if(walletBalanceOptional.isPresent()) {
            var balance = walletBalanceOptional.get();
            return WalletBalanceResponse.builder()
                    .address(balance.getAddress())
                    .balance(balance.getBalance().toString())
                    .build();
        }
        throw new RuntimeException("해당 주소의 지갑이 존재하지 않습니다");
    }
}
