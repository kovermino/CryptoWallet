package com.joel.cryptowallet.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository sut;

    @Test
    void save_사용자_정보를_저장한다() {
        UserEntity user = UserEntity.builder()
                .walletId("walletId")
                .password("password")
                .walletAddress("ethereumAccount")
                .privateKey("ethereumAccount")
                .status(UserStatus.ACTIVATED)
                .build();


        sut.save(user);


        assertTrue(sut.findById("walletId").isPresent());
    }
}