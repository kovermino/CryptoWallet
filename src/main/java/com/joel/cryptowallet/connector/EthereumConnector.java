package com.joel.cryptowallet.connector;

import com.joel.cryptowallet.transaction.domain.EthTxPerAddress;
import com.joel.cryptowallet.transaction.domain.EthTxTotal;
import com.joel.cryptowallet.transaction.domain.EthTxRecord;
import com.joel.cryptowallet.transaction.domain.enums.TransactionStatus;
import com.joel.cryptowallet.transaction.domain.enums.TransactionType;
import com.joel.cryptowallet.wallet.domain.dto.AccountDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EthereumConnector implements BlockChainConnector {

    private final Web3j web3j;

    @Override
    public AccountDTO createAccount() {
        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            String address = "0x" + Keys.getAddress(ecKeyPair);
            String privateKey = ecKeyPair.getPrivateKey().toString(16);
            BigInteger lastCheckedNode = getLatestNode();
            return AccountDTO.builder()
                    .address(address)
                    .privateKey(privateKey)
                    .lastCheckedNode(lastCheckedNode)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("이더리움 지갑 생성 중 에러가 발생했습니다");
        }
    }

    @Override
    public EthTxTotal retrieveTransactions(BigInteger startBlockNodeNumber) {
        BigInteger latestBlockNumber = getLatestNode();

        EthTxTotal txTotal = EthTxTotal.builder()
                .lastCheckedNode(latestBlockNumber)
                .transactionsWithAddress(new HashMap<>())
                .build();

        for (long currentBlockNumber = startBlockNodeNumber.longValue(); currentBlockNumber <= latestBlockNumber.longValue(); currentBlockNumber++) {
                EthBlock.Block block = getEthBlockInfo(currentBlockNumber);
                if (block != null && block.getTransactions() != null) {
                    List<Transaction> transactions = block.getTransactions().stream().map(txResults -> (Transaction) txResults.get()).collect(Collectors.toList());
                    TransactionStatus transactionStatus = getTransactionStatus(latestBlockNumber.longValue(), currentBlockNumber);
                    appendToEthTxTotal(txTotal, transactions, transactionStatus, currentBlockNumber);
                }
        }

        return txTotal;
    }

    private BigInteger getLatestNode() {
        BigInteger latestBlockNumber;
        try {
            latestBlockNumber = web3j.ethBlockNumber().sendAsync().get().getBlockNumber();
        } catch (Exception e) {
            throw new RuntimeException("최신 블록 노드 번호를 가져오는데 실패했습니다");
        }
        return latestBlockNumber;
    }

    private EthBlock.Block getEthBlockInfo(long blockNumber) {
        EthBlock.Block block;
        try {
            block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), true).sendAsync().get().getBlock();
        } catch (Exception e) {
            log.error(blockNumber + "번 블록의 정보를 가져오지 못했습니다.");
            e.printStackTrace();
            return null;
        }
        return block;
    }

    private TransactionStatus getTransactionStatus(long latestNodeNumber, long currentNodeNumber) {
        long distance = latestNodeNumber - currentNodeNumber;
        if(distance > 11) {
            return TransactionStatus.CONFIRMED;
        }
        return TransactionStatus.MINDED;
    }

    private void appendToEthTxTotal(EthTxTotal txTotal, List<Transaction> transactions, TransactionStatus transactionStatus, long recordedBlockNumber) {
        for (Transaction tx : transactions) {
            String from = tx.getFrom();
            String to = tx.getTo();

            var withdrawalTx = appendEthTxSummary(txTotal, tx, from, TransactionType.WITHDRAWAL, transactionStatus, recordedBlockNumber);
            var depositTx = appendEthTxSummary(txTotal, tx, to, TransactionType.DEPOSIT, transactionStatus, recordedBlockNumber);

            txTotal.getTransactionsWithAddress().put(from, withdrawalTx);
            txTotal.getTransactionsWithAddress().put(to, depositTx);
        }
    }

    private EthTxPerAddress appendEthTxSummary(
            EthTxTotal txTotal,
            Transaction tx,
            String address,
            TransactionType transactionType,
            TransactionStatus transactionStatus,
            long recordedBlockNumber
    ) {
        var withdrawalTx = txTotal.getTransactionsWithAddress().getOrDefault(
                address,
                new EthTxPerAddress(address)
        );
        withdrawalTx.addTransaction(
                buildEthTxRecord(tx, transactionType, transactionStatus, recordedBlockNumber)
        );
        return withdrawalTx;
    }

    private EthTxRecord buildEthTxRecord(
            Transaction tx,
            TransactionType transactionType,
            TransactionStatus transactionStatus,
            long recordedBlockNumber
    ) {
        return EthTxRecord.builder()
                .txHash(tx.getHash())
                .transactionType(transactionType)
                .transactionStatus(transactionStatus)
                .from(tx.getFrom())
                .to(tx.getTo())
                .amount(tx.getValue())
                .recordedBlockNode(BigInteger.valueOf(recordedBlockNumber))
                .build();
    }

}
