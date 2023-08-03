package com.joel.cryptowallet.wallet;

import com.joel.cryptowallet.connector.EthereumConnector;
import com.joel.cryptowallet.wallet.domain.entity.WalletBalanceEntity;
import com.joel.cryptowallet.wallet.domain.entity.WalletUserEntity;
import com.joel.cryptowallet.wallet.repository.WalletBalanceRepository;
import com.joel.cryptowallet.wallet.repository.WalletUserRepository;
import com.joel.cryptowallet.wallet.domain.enums.WalletUserStatus;
import com.joel.cryptowallet.wallet.domain.dto.AccountDTO;
import com.joel.cryptowallet.wallet.service.BlockChainWalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BlockChainWalletServiceTest {

    private BlockChainWalletService sut;
    private WalletUserRepository walletUserRepository;
    private WalletBalanceRepository walletBalanceRepository;
    private EthereumConnector ethereumConnector;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        walletUserRepository = mock(WalletUserRepository.class);
        walletBalanceRepository = mock(WalletBalanceRepository.class);
        ethereumConnector = mock(EthereumConnector.class);
        passwordEncoder = mock(PasswordEncoder.class);
        sut = new BlockChainWalletService(
                walletUserRepository,
                walletBalanceRepository,
                ethereumConnector,
                passwordEncoder
        );
    }

    @Test
    void createWallet_계정정보와_잔액을_저장하고_이더리움_지갑을_생성한다() {
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

        verify(walletBalanceRepository).save(WalletBalanceEntity.builder()
                        .walletId(id)
                        .address(address)
                        .balance(BigInteger.ZERO)
                .build()
        );
    }

    @Test
    void createWallet_동일한_아이디가_있는_경우에는_에러를_발생시킨다() {
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
        when(walletUserRepository.existsById(id)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            sut.createWallet(id, password);
        });
    }

    @Test
    void getBalance_해당지갑의_잔액을_데이터베이스에서_조회하여_리턴한다() {
        String walletAddress = "sampleWalletAddress";
        when(walletBalanceRepository.findByAddress("sampleWalletAddress")).thenReturn(
                Optional.of(
                        WalletBalanceEntity.builder()
                                .address(walletAddress)
                                .balance(BigInteger.valueOf(1234))
                                .build()
                )
        );


        var result = sut.getBalance(walletAddress);


        assertEquals(walletAddress, result.address());
        assertEquals("1234", result.balance());
    }

    @Test
    void getBalance_해당지갑이_없으면_에러를_발생시킨다() {
        String walletAddress = "sampleWalletAddress";
        when(walletBalanceRepository.findByAddress("sampleWalletAddress")).thenReturn(
                Optional.empty()
        );


        assertThrows(RuntimeException.class, () -> {
           sut.getBalance(walletAddress);
        });
    }
}