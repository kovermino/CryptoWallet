package com.joel.cryptowallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {

    private final String blockChainUrl = "https://tn.henesis.io/ethereum/goerli?clientId=815fcd01324b8f75818a755a72557750";

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(blockChainUrl));
    }
}
