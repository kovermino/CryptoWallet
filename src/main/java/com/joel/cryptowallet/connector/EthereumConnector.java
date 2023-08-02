package com.joel.cryptowallet.connector;

import com.joel.cryptowallet.wallet.domain.dto.AccountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;

@Slf4j
@Service
public class EthereumConnector implements BlockChainConnector {

    @Override
    public AccountDTO createAccount() {
        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            String address = "0x" + Keys.getAddress(ecKeyPair);
            String privateKey = ecKeyPair.getPrivateKey().toString(16);
            return AccountDTO.builder()
                    .address(address)
                    .privateKey(privateKey)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("이더리움 지갑 생성 중 에러가 발생했습니다");
        }
    }
}
