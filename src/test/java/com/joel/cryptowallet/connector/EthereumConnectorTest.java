package com.joel.cryptowallet.connector;

import com.joel.cryptowallet.transaction.domain.EthTxPerAddress;
import com.joel.cryptowallet.transaction.domain.EthTxTotal;
import com.joel.cryptowallet.transaction.domain.EthTxRecord;
import com.joel.cryptowallet.transaction.domain.enums.TransactionStatus;
import com.joel.cryptowallet.transaction.domain.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EthereumConnectorTest {
    private EthereumConnector sut;
    private Web3j web3j;

    @BeforeEach
    void setUp() {
        web3j = mock(Web3j.class);
        sut = new EthereumConnector(web3j);
    }

    @Test
    void createAccount_올바른_형식의_이더리움_지갑주소와_개인키를_생성하고_최신_이더리움_노드_번호를_리턴한다() {
        stubLatestBlockNumber(1234);


        var result = sut.createAccount();


        assertTrue(result.address().startsWith("0x"));
        assertEquals(42, result.address().length());
        assertEquals(64, result.privateKey().length());
        assertEquals(BigInteger.valueOf(1234), result.lastCheckedNode());
    }

    @Test
    void retrieveTransactions_노드의_거래내역을_조회한다() {
        stubLatestBlockNumber(2);
        stubGetBlockByNumber(2, "tx1", "address1", "address2", "1");


        var transactions = sut.retrieveTransactions(BigInteger.TWO);


        var address1Tx = new EthTxPerAddress("address1");
        address1Tx.addTransaction(
                EthTxRecord.builder()
                        .txHash("tx1")
                        .transactionType(TransactionType.WITHDRAWAL)
                        .transactionStatus(TransactionStatus.MINDED)
                        .from("address1")
                        .to("address2")
                        .amount(BigInteger.ONE)
                        .recordedBlockNode(BigInteger.valueOf(2))
                        .build()
        );
        var address2Tx = new EthTxPerAddress("address2");
        address2Tx.addTransaction(
                EthTxRecord.builder()
                        .txHash("tx1")
                        .transactionType(TransactionType.DEPOSIT)
                        .transactionStatus(TransactionStatus.MINDED)
                        .from("address1")
                        .to("address2")
                        .amount(BigInteger.ONE)
                        .recordedBlockNode(BigInteger.valueOf(2))
                        .build()
        );
        var expected = EthTxTotal.builder()
                .lastCheckedNode(BigInteger.valueOf(2))
                .transactionsWithAddress(
                        Map.of(
                                "address1", address1Tx,
                                "address2", address2Tx
                        )
                )
                .build();
        assertEquals(expected, transactions);
    }

    protected void stubLatestBlockNumber(int blockNumber) {
        Web3jService web3jService = mock(Web3jService.class);
        var stubBlockNumberResult = new EthBlockNumber();
        stubBlockNumberResult.setResult(String.valueOf(blockNumber));
        when(web3jService.sendAsync(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> stubBlockNumberResult));
        when(web3j.ethBlockNumber()).thenReturn(new Request<>(null, null, web3jService, EthBlockNumber.class));
    }

    protected void stubGetBlockByNumber(int blockNumber, String txHash, String from, String to, String amount) {
        var ethBlock = new EthBlock();
        var block = new EthBlock.Block();
        block.setTransactions(List.of(
                new EthBlock.TransactionObject(txHash, null, null, String.valueOf(blockNumber), null, null, from, to, amount, null, null, null, null, null, null, null, null, 0, null, null, null, null)
        ));
        ethBlock.setResult(block);
        Web3jService web3jService = mock(Web3jService.class);
        when(web3jService.sendAsync(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> ethBlock));
        when(web3j.ethGetBlockByNumber(any(), eq(true))).thenReturn(new Request<>(null, null, web3jService, EthBlock.class));
    }
}