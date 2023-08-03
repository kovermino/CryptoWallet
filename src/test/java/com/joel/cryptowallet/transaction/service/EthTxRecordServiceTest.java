package com.joel.cryptowallet.transaction.service;

import com.joel.cryptowallet.connector.EthereumConnector;
import com.joel.cryptowallet.transaction.domain.dto.EthTxRecord;
import com.joel.cryptowallet.transaction.domain.EthTxSummaryPerAddress;
import com.joel.cryptowallet.transaction.domain.EthTxTotal;
import com.joel.cryptowallet.transaction.domain.entity.EthTransactionEntity;
import com.joel.cryptowallet.transaction.domain.enums.TransactionType;
import com.joel.cryptowallet.transaction.repository.EthTransactionRepository;
import com.joel.cryptowallet.wallet.domain.entity.WalletBalanceEntity;
import com.joel.cryptowallet.wallet.repository.WalletBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class EthTxRecordServiceTest {
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
    void updateTransactions() {
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

        when(ethereumConnector.retrieveTransactions(BigInteger.ZERO)).thenReturn(
                EthTxTotal.builder()
                        .lastCheckedNode(BigInteger.valueOf(20))
                        .transactionsWithAddress(
                                Map.of(
                                        "address1", EthTxSummaryPerAddress.builder()
                                                .address("address1")
                                                .balance(BigInteger.ZERO)
                                                .transactionList(List.of())
                                                .build(),
                                        "address2", EthTxSummaryPerAddress.builder()
                                                .address("address2")
                                                .balance(BigInteger.TWO)
                                                .transactionList(
                                                        List.of(
                                                                EthTxRecord.builder()
                                                                        .txHash("x1")
                                                                        .transactionType(TransactionType.DEPOSIT)
                                                                        .from("address3")
                                                                        .to("address2")
                                                                        .amount(BigInteger.ONE)
                                                                        .recordedBlockNode(BigInteger.valueOf(3))
                                                                        .build()
                                                        )
                                                )
                                                .build(),
                                        "address3", EthTxSummaryPerAddress.builder()
                                                .address("address3")
                                                .balance(BigInteger.ONE)
                                                .transactionList(
                                                        List.of(
                                                                EthTxRecord.builder()
                                                                        .txHash("x2")
                                                                        .transactionType(TransactionType.WITHDRAWAL)
                                                                        .from("address3")
                                                                        .to("address2")
                                                                        .amount(BigInteger.ONE)
                                                                        .recordedBlockNode(BigInteger.valueOf(3))
                                                                        .build()
                                                        )
                                                )
                                                .build()
                                )
                        )
                        .build()
        );


        sut.update();


        verify(walletBalanceRepository).saveAll(
                List.of(
                        WalletBalanceEntity.builder()
                                .walletId("wallet1")
                                .address("address1")
                                .balance(BigInteger.ZERO)
                                .lastCheckedNode(BigInteger.valueOf(20))
                                .build(),
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

        verify(ethTransactionRepository).saveAll(
                List.of(
                        EthTransactionEntity.builder()
                                .txHash("x1")
                                .transactionType(TransactionType.DEPOSIT)
                                .from("address3")
                                .to("address2")
                                .amount(BigInteger.ONE)
                                .recordedBlockNode(BigInteger.valueOf(3))
                                .build(),
                        EthTransactionEntity.builder()
                                .txHash("x2")
                                .transactionType(TransactionType.WITHDRAWAL)
                                .from("address3")
                                .to("address2")
                                .amount(BigInteger.ONE)
                                .recordedBlockNode(BigInteger.valueOf(3))
                                .build()
                        )
        );
    }
}