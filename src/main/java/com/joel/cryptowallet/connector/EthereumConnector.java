package com.joel.cryptowallet.connector;

import com.joel.cryptowallet.transaction.domain.EthTxSummaryPerAddress;
import com.joel.cryptowallet.transaction.domain.EthTxTotal;
import com.joel.cryptowallet.transaction.domain.dto.EthTxRecord;
import com.joel.cryptowallet.transaction.domain.enums.TransactionType;
import com.joel.cryptowallet.wallet.domain.dto.AccountDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.Transaction;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

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
            BigInteger lastCheckedNode = web3j.ethBlockNumber().sendAsync().get().getBlockNumber();
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
        BigInteger latestBlockNumber;
        try {
            latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        } catch (IOException e) {
            throw new RuntimeException("최신 블록 노드 번호를 가져오는데 실패했습니다");
        }

        EthTxTotal txTotal = EthTxTotal.builder()
                .lastCheckedNode(latestBlockNumber)
                .transactionsWithAddress(new HashMap<>())
                .build();
        for (long currentBlockNumber = startBlockNodeNumber.longValue(); currentBlockNumber <= latestBlockNumber.longValue(); currentBlockNumber++) {
            try {
                EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(latestBlockNumber), true).send().getBlock();

                if (block != null && block.getTransactions() != null) {
                    for (EthBlock.TransactionResult<Transaction> txResult : block.getTransactions()) {
                        Transaction tx = txResult.get();
                        String from = tx.getFrom();
                        String to = tx.getTo();

                        var withdrawalTx = txTotal.getTransactionsWithAddress().getOrDefault(from,
                                EthTxSummaryPerAddress.builder()
                                        .address(from)
                                        .transactionList(List.of())
                                        .build()
                        );
                        withdrawalTx.getTransactionList().add(
                                EthTxRecord.builder()
                                        .txHash(tx.getHash())
                                        .transactionType(TransactionType.WITHDRAWAL)
                                        .from(from)
                                        .to(to)
                                        .amount(tx.getValue())
                                        .recordedBlockNode(BigInteger.valueOf(currentBlockNumber))
                                        .build()
                        );

                        var depositTx = txTotal.getTransactionsWithAddress().getOrDefault(to,
                                EthTxSummaryPerAddress.builder()
                                        .address(from)
                                        .transactionList(List.of())
                                        .build()
                        );
                        depositTx.getTransactionList().add(
                                EthTxRecord.builder()
                                        .txHash(tx.getHash())
                                        .transactionType(TransactionType.DEPOSIT)
                                        .from(from)
                                        .to(to)
                                        .amount(tx.getValue())
                                        .recordedBlockNode(BigInteger.valueOf(currentBlockNumber))
                                        .build()
                        );

                        txTotal.getTransactionsWithAddress().put(from, withdrawalTx);
                        txTotal.getTransactionsWithAddress().put(to, depositTx);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return txTotal;
    }

}
