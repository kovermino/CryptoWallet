package com.joel.cryptowallet.wallet;

import com.joel.cryptowallet.wallet.domain.entity.WalletBalanceEntity;
import com.joel.cryptowallet.wallet.repository.WalletBalanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WalletBalanceRepositoryTest {

    @Autowired
    private WalletBalanceRepository sut;

    @Test
    void save() {
        sut.save(WalletBalanceEntity.getInitialBalance("sampleWalletId"));


        assertTrue(sut.findById("sampleWalletId").isPresent());
    }
}