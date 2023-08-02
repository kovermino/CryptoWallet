package com.joel.cryptowallet.wallet.repository;

import com.joel.cryptowallet.wallet.domain.entity.WalletUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletUserRepository extends JpaRepository<WalletUserEntity, String> {
}
