package com.joel.cryptowallet.wallet;

import com.joel.cryptowallet.connector.EthereumConnector;
import com.joel.cryptowallet.wallet.domain.entity.WalletUserEntity;
import com.joel.cryptowallet.wallet.repository.WalletUserRepository;
import com.joel.cryptowallet.wallet.domain.enums.WalletUserStatus;
import com.joel.cryptowallet.wallet.domain.dto.AccountDTO;
import com.joel.cryptowallet.wallet.service.BlockChainWalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BlockChainWalletServiceTest {

    private BlockChainWalletService sut;
    private WalletUserRepository walletUserRepository;
    private EthereumConnector ethereumConnector;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        walletUserRepository = mock(WalletUserRepository.class);
        ethereumConnector = mock(EthereumConnector.class);
        passwordEncoder = mock(PasswordEncoder.class);
        sut = new BlockChainWalletService(
                walletUserRepository,
                ethereumConnector,
                passwordEncoder
        );
    }

    @Test
    void createEthereumWallet_계정정보를_저장하고_이더리움_지갑을_생성한다() {
        String id = "sampleWalletId";
        String password = "sampleWalletPassword";
        String address = "sampleEthereumAccountAddress";
        String privateKey = "sampleEthereumPrivateKey";
        when(ethereumConnector.createAccount()).thenReturn(
                AccountDTO.builder()
                        .address(address)
                        .privateKey(privateKey)
                        .build()
        );
        when(passwordEncoder.encode(password)).thenReturn("encryptedPassword");
        when(passwordEncoder.encode(privateKey)).thenReturn("encryptedPrivateKey");


        var result = sut.createWallet(id, password);


        assertEquals(id, result.id());
        assertEquals(address, result.address());
        assertEquals(privateKey, result.privateKey());

        verify(walletUserRepository).save(
                WalletUserEntity.builder()
                        .walletId(id)
                        .password("encryptedPassword")
                        .walletAddress(address)
                        .privateKey("encryptedPrivateKey")
                        .status(WalletUserStatus.ACTIVATED)
                        .build()
        );
    }
}