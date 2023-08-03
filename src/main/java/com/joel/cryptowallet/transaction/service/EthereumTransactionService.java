package com.joel.cryptowallet.transaction.service;

import com.joel.cryptowallet.connector.EthereumConnector;
import com.joel.cryptowallet.transaction.domain.entity.EthTransactionEntity;
import com.joel.cryptowallet.transaction.repository.EthTransactionRepository;
import com.joel.cryptowallet.wallet.repository.WalletBalanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EthereumTransactionService {
    private final WalletBalanceRepository walletBalanceRepository;
    private final EthTransactionRepository ethTransactionRepository;
    private final EthereumConnector ethereumConnector;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void update() {
        var allBalanceList = walletBalanceRepository.findAll();
        var oldestCheckedNode = allBalanceList.stream().mapToLong(balance -> balance.getLastCheckedNode().longValue()).min().orElse(-1);
        if(oldestCheckedNode == -1) {
            log.info("갱신할 지갑이 없습니다");
            return;
        }
        var ethTransactionInfo = ethereumConnector.retrieveTransactions(BigInteger.valueOf(oldestCheckedNode));
        var txsPerAddressMap = ethTransactionInfo.getTransactionsWithAddress();
        allBalanceList.forEach(balanceEntity -> {
            var originalBalance = balanceEntity.getBalance();
            var txInfo = txsPerAddressMap.get(balanceEntity.getAddress());
            var txAmount = txInfo.totalTxAmount(ethTransactionInfo.getLastCheckedNode());
            balanceEntity.setBalance(originalBalance.add(txAmount));
            balanceEntity.setLastCheckedNode(ethTransactionInfo.getLastCheckedNode());
        });
        walletBalanceRepository.saveAll(allBalanceList);

        List<EthTransactionEntity> allTransactions = txsPerAddressMap.values().stream()
                .map(tx -> tx.getTransactionList())
                .map(txList ->
                        txList.stream().map(tx -> tx.toEthTransactionEntity())
                                .collect(Collectors.toList())
                )
                .flatMap(List:: stream)
                .collect(Collectors.toList());
        ethTransactionRepository.saveAll(allTransactions);
    }
}
