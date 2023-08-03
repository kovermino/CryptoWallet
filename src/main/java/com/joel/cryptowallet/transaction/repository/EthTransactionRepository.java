package com.joel.cryptowallet.transaction.repository;

import com.joel.cryptowallet.transaction.domain.entity.EthTransactionEntity;
import com.joel.cryptowallet.transaction.domain.entity.EthTransactionEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EthTransactionRepository extends JpaRepository<EthTransactionEntity, EthTransactionEntityId> {
}
