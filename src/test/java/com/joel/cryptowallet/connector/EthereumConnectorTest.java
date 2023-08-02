package com.joel.cryptowallet.connector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EthereumConnectorTest {
    private EthereumConnector sut;

    @BeforeEach
    void setUp() {
        sut = new EthereumConnector();
    }

    @Test
    void createAccount_올바른_형식의_이더리움_지갑주소와_개인키를_생성한다() {
        var result = sut.createAccount();


        assertTrue(result.address().startsWith("0x"));
        assertEquals(42, result.address().length());
        assertEquals(64, result.privateKey().length());
    }
}