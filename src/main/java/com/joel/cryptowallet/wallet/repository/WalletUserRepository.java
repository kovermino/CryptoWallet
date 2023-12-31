package com.joel.cryptowallet.wallet.repository;

import com.joel.cryptowallet.wallet.domain.entity.WalletUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletUserRepository extends JpaRepository<WalletUserEntity, String> {
}
