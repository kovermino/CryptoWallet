package com.joel.cryptowallet.connector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EthereumConnectorTest {
    private EthereumConnector sut;
    private Web3j web3j;
    private Web3jService web3jService;

    @BeforeEach
    void setUp() {
        web3jService = mock(Web3jService.class);
        web3j = mock(Web3j.class);
        sut = new EthereumConnector(web3j);
    }

    @Test
    void createAccount_올바른_형식의_이더리움_지갑주소와_개인키를_생성하고_최신_이더리움_노드_번호를_리턴한다() {
        var stubBlockNumberResult = new EthBlockNumber();
        stubBlockNumberResult.setResult("1234");
        when(web3jService.sendAsync(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> stubBlockNumberResult));
        when(web3j.ethBlockNumber()).thenReturn(new Request<>(null, null, web3jService, EthBlockNumber.class));


        var result = sut.createAccount();


        assertTrue(result.address().startsWith("0x"));
        assertEquals(42, result.address().length());
        assertEquals(64, result.privateKey().length());
        assertEquals(BigInteger.valueOf(1234), result.lastCheckedNode());
    }
}