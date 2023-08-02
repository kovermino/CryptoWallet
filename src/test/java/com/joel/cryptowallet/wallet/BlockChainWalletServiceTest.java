package com.joel.cryptowallet.wallet;

import com.joel.cryptowallet.connector.EthereumConnector;
import com.joel.cryptowallet.user.UserEntity;
import com.joel.cryptowallet.user.UserRepository;
import com.joel.cryptowallet.user.UserStatus;
import com.joel.cryptowallet.user.dto.AccountDTO;
import com.joel.cryptowallet.wallet.service.BlockChainWalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BlockChainWalletServiceTest {

    private BlockChainWalletService sut;
    private UserRepository userRepository;
    private EthereumConnector ethereumConnector;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        ethereumConnector = mock(EthereumConnector.class);
        sut = new BlockChainWalletService(
                userRepository,
                ethereumConnector
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


        var result = sut.createWallet(id, password);


        assertEquals(id, result.id());
        assertEquals(address, result.address());
        assertEquals(privateKey, result.privateKey());

        verify(userRepository).save(
                UserEntity.builder()
                        .walletId(id)
                        .password(password)
                        .walletAddress(address)
                        .privateKey(privateKey)
                        .status(UserStatus.ACTIVATED)
                        .build()
        );
    }
}