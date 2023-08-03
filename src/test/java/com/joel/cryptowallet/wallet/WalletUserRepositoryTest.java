package com.joel.cryptowallet.wallet;

import com.joel.cryptowallet.wallet.domain.entity.WalletUserEntity;
import com.joel.cryptowallet.wallet.domain.enums.WalletUserStatus;
import com.joel.cryptowallet.wallet.repository.WalletUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WalletUserRepositoryTest {

    @Autowired
    private WalletUserRepository sut;

    @Test
    void save_사용자_정보를_저장한다() {
        WalletUserEntity user = WalletUserEntity.builder()
                .walletId("walletId")
                .password("password")
                .walletAddress("ethereumAccount")
                .privateKey("ethereumAccount")
                .status(WalletUserStatus.ACTIVATED)
                .build();


        sut.save(user);


        assertTrue(sut.existsById("walletId"));
    }

    @Test
    void existsById_데이터가_없는_경우_false를_리턴한다() {
        assertFalse(sut.existsById("notExistWalletId"));
    }
}