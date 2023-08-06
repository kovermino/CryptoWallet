package com.joel.cryptowallet.transaction.service;

import com.joel.cryptowallet.connector.EthereumConnector;
import com.joel.cryptowallet.transaction.domain.EthTxPerAddress;
import com.joel.cryptowallet.transaction.domain.entity.EthTransactionEntity;
import com.joel.cryptowallet.transaction.domain.enums.TransactionStatus;
import com.joel.cryptowallet.transaction.repository.EthTransactionRepository;
import com.joel.cryptowallet.wallet.domain.entity.WalletBalanceEntity;
import com.joel.cryptowallet.wallet.repository.WalletBalanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EthereumTransactionService {
    private final WalletBalanceRepository walletBalanceRepository;
    private final EthTransactionRepository ethTransactionRepository;
    private final EthereumConnector ethereumConnector;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void update() {
        var allBalanceList = walletBalanceRepository.findAll();
        var oldestCheckedNode = getOldestCheckNode(allBalanceList);
        if (oldestCheckedNode == null) {
            log.info("갱신할 지갑이 없습니다");
            return;
        }
        var ethTransactionInfo = ethereumConnector.retrieveTransactions(BigInteger.valueOf(oldestCheckedNode));
        var txsPerAddressMap = ethTransactionInfo.getTransactionsWithAddress();
        updateBalances(allBalanceList, oldestCheckedNode, txsPerAddressMap);

        Set<String> henesisWalletAddressSet = allBalanceList.stream().map(balanceEntity -> balanceEntity.getAddress()).collect(Collectors.toSet());
        insertRecentTransactions(henesisWalletAddressSet, txsPerAddressMap);
    }

    private Long getOldestCheckNode(List<WalletBalanceEntity> allBalanceList) {
        var oldestCheckNode = allBalanceList.stream().mapToLong(balance -> balance.getLastCheckedNode().longValue()).min().orElse(-1);
        if(oldestCheckNode < 0) {
            return null;
        }
        return oldestCheckNode;
    }

    private void updateBalances(List<WalletBalanceEntity> allBalanceList, Long checkStartNode, Map<String, EthTxPerAddress> txsPerAddressMap) {
        var ethTransactionInfo = ethereumConnector.retrieveTransactions(BigInteger.valueOf(checkStartNode));
        List<WalletBalanceEntity> updatedBalances = allBalanceList.stream()
                .filter(balanceEntity -> txsPerAddressMap.containsKey(balanceEntity.getAddress()))
                .filter(balanceEntity -> txsPerAddressMap.get(balanceEntity.getAddress()).getTransactionList().stream().anyMatch(tx -> tx.transactionStatus() == TransactionStatus.CONFIRMED))
                .map(balanceEntity -> {
                    var originalBalance = balanceEntity.getBalance();
                    var txInfo = txsPerAddressMap.get(balanceEntity.getAddress());
                    var txAmount = txInfo.totalTxAmount(balanceEntity.getLastCheckedNode(), ethTransactionInfo.getLastCheckedNode());
                    balanceEntity.setBalance(originalBalance.add(txAmount));
                    balanceEntity.setLastCheckedNode(ethTransactionInfo.getLastCheckedNode());
                    return balanceEntity;
                })
                .collect(Collectors.toList());
        if(!updatedBalances.isEmpty()) {
            walletBalanceRepository.saveAll(updatedBalances);
            log.info(updatedBalances.size() + "건의 지갑 잔액 갱신이 감지되었습니다.");
        } else {
            log.info("관리되고 있는 지갑에 해당하는 잔액 업데이트가 없습니다.");
        }
    }

    private void insertRecentTransactions(Set<String> henesisWalletAddressSet, Map<String, EthTxPerAddress> txsPerAddressMap) {
        List<EthTransactionEntity> allTransactions = txsPerAddressMap.values().stream()
                .map(tx -> tx.getTransactionList())
                .map(txList -> txList.stream().filter(tx -> henesisWalletAddressSet.contains(tx.from()) || henesisWalletAddressSet.contains(tx.to())).collect(Collectors.toList()))
                .map(txList ->
                        txList.stream().map(tx -> tx.toEthTransactionEntity())
                                .collect(Collectors.toList())
                )
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if(!allTransactions.isEmpty()) {
            ethTransactionRepository.saveAll(allTransactions);
        } else {
            log.info("관리되고 있는 지갑에 해당하는 거래 업데이트가 없습니다.");
        }
    }
}
