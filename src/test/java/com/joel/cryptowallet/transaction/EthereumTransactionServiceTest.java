package com.joel.cryptowallet.transaction;

import com.joel.cryptowallet.connector.EthereumConnector;
import com.joel.cryptowallet.transaction.domain.EthTxRecord;
import com.joel.cryptowallet.transaction.domain.EthTxPerAddress;
import com.joel.cryptowallet.transaction.domain.EthTxTotal;
import com.joel.cryptowallet.transaction.domain.entity.EthTransactionEntity;
import com.joel.cryptowallet.transaction.domain.enums.TransactionStatus;
import com.joel.cryptowallet.transaction.domain.enums.TransactionType;
import com.joel.cryptowallet.transaction.repository.EthTransactionRepository;
import com.joel.cryptowallet.transaction.service.EthereumTransactionService;
import com.joel.cryptowallet.wallet.domain.entity.WalletBalanceEntity;
import com.joel.cryptowallet.wallet.repository.WalletBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class EthereumTransactionServiceTest {
    private EthereumTransactionService sut;
    private WalletBalanceRepository walletBalanceRepository;
    private EthTransactionRepository ethTransactionRepository;
    private EthereumConnector ethereumConnector;

    @BeforeEach
    void setUp() {
        walletBalanceRepository = mock(WalletBalanceRepository.class);
        ethTransactionRepository = mock(EthTransactionRepository.class);
        ethereumConnector = mock(EthereumConnector.class);
        sut = new EthereumTransactionService(
                walletBalanceRepository,
                ethTransactionRepository,
                ethereumConnector
        );
    }

    @Test
    void update_이더리움_네트워크에서_조회된_입출금_내역을_기록한다() {
        when(walletBalanceRepository.findAll()).thenReturn(
                List.of(
                        WalletBalanceEntity.builder()
                                .walletId("wallet1")
                                .address("address1")
                                .balance(BigInteger.ZERO)
                                .lastCheckedNode(BigInteger.ONE)
                                .build(),
                        WalletBalanceEntity.builder()
                                .walletId("wallet2")
                                .address("address2")
                                .balance(BigInteger.ZERO)
                                .lastCheckedNode(BigInteger.ZERO)
                                .build(),
                        WalletBalanceEntity.builder()
                                .walletId("wallet3")
                                .address("address3")
                                .balance(BigInteger.TWO)
                                .lastCheckedNode(BigInteger.TWO)
                                .build()
                )
        );

        var address2Tx = new EthTxPerAddress("address2");
        address2Tx.addTransaction(
                EthTxRecord.builder()
                        .txHash("x1")
                        .transactionType(TransactionType.DEPOSIT)
                        .transactionStatus(TransactionStatus.CONFIRMED)
                        .from("address3")
                        .to("address2")
                        .amount(BigInteger.ONE)
                        .recordedBlockNode(BigInteger.valueOf(3))
                        .build()
        );
        var address3Tx = new EthTxPerAddress("address3");
        address3Tx.addTransaction(
                EthTxRecord.builder()
                        .txHash("x2")
                        .transactionType(TransactionType.WITHDRAWAL)
                        .transactionStatus(TransactionStatus.CONFIRMED)
                        .from("address3")
                        .to("address2")
                        .amount(BigInteger.ONE)
                        .recordedBlockNode(BigInteger.valueOf(3))
                        .build()
        );
        when(ethereumConnector.retrieveTransactions(BigInteger.ZERO)).thenReturn(
                EthTxTotal.builder()
                        .lastCheckedNode(BigInteger.valueOf(20))
                        .transactionsWithAddress(
                                Map.of(
                                        "address2", address2Tx,
                                        "address3", address3Tx
                                )
                        )
                        .build()
        );


        sut.update();


        verify(walletBalanceRepository).saveAll(
                List.of(
                        WalletBalanceEntity.builder()
                                .walletId("wallet2")
                                .address("address2")
                                .balance(BigInteger.ONE)
                                .lastCheckedNode(BigInteger.valueOf(20))
                                .build(),
                        WalletBalanceEntity.builder()
                                .walletId("wallet3")
                                .address("address3")
                                .balance(BigInteger.ONE)
                                .lastCheckedNode(BigInteger.valueOf(20))
                                .build()
                )
        );

        verify(ethTransactionRepository).saveAll(argThat(list ->
                new HashSet<>(List.of(
                        EthTransactionEntity.builder()
                                .txHash("x1")
                                .transactionType(TransactionType.DEPOSIT)
                                .transactionStatus(TransactionStatus.CONFIRMED)
                                .departure("address3")
                                .destination("address2")
                                .amount(BigInteger.ONE)
                                .recordedBlockNode(BigInteger.valueOf(3))
                                .build(),
                        EthTransactionEntity.builder()
                                .txHash("x2")
                                .transactionType(TransactionType.WITHDRAWAL)
                                .transactionStatus(TransactionStatus.CONFIRMED)
                                .departure("address3")
                                .destination("address2")
                                .amount(BigInteger.ONE)
                                .recordedBlockNode(BigInteger.valueOf(3))
                                .build()
                )).equals(new HashSet<>((Collection) list)))
        );
    }

    @Test
    void update_이더리움_네트워크에_해당_지갑의_기록이_없으면_기록하지_않는다() {
        when(walletBalanceRepository.findAll()).thenReturn(
                List.of(
                        WalletBalanceEntity.builder()
                                .walletId("wallet1")
                                .address("address1")
                                .balance(BigInteger.ZERO)
                                .lastCheckedNode(BigInteger.ZERO)
                                .build()
                )
        );

        var address2Tx = new EthTxPerAddress("address2");
        address2Tx.addTransaction(
                EthTxRecord.builder()
                        .txHash("x1")
                        .transactionType(TransactionType.DEPOSIT)
                        .from("address3")
                        .to("address2")
                        .amount(BigInteger.ONE)
                        .recordedBlockNode(BigInteger.valueOf(3))
                        .build()
        );
        var address3Tx = new EthTxPerAddress("address3");
        address3Tx.addTransaction(
                EthTxRecord.builder()
                        .txHash("x2")
                        .transactionType(TransactionType.WITHDRAWAL)
                        .from("address3")
                        .to("address2")
                        .amount(BigInteger.ONE)
                        .recordedBlockNode(BigInteger.valueOf(3))
                        .build()
        );
        when(ethereumConnector.retrieveTransactions(BigInteger.ZERO)).thenReturn(
                EthTxTotal.builder()
                        .lastCheckedNode(BigInteger.valueOf(20))
                        .transactionsWithAddress(
                                Map.of(
                                        "address2", address2Tx,
                                        "address3", address3Tx
                                )
                        )
                        .build()
        );


        sut.update();


        verify(walletBalanceRepository, never()).saveAll(any());
        verify(ethTransactionRepository, never()).saveAll(any());
    }
}