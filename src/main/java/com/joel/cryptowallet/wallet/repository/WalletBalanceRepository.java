package com.joel.cryptowallet.wallet.repository;

import com.joel.cryptowallet.wallet.domain.entity.WalletBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletBalanceRepository extends JpaRepository<WalletBalanceEntity, String> {
}
