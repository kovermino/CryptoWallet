package com.joel.cryptowallet.wallet.repository;

import com.joel.cryptowallet.wallet.domain.entity.WalletBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletBalanceRepository extends JpaRepository<WalletBalanceEntity, String> {
    Optional<WalletBalanceEntity> findByAddress(String address);
}
