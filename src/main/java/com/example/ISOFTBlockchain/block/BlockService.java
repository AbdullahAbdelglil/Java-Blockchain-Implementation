package com.example.ISOFTBlockchain.block;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BlockService {
    private final BlockRepository blockRepository;

    public BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    public void save(Block block) {
        blockRepository.save(block);
    }

    public List<Block> getBlockchain() {
        return blockRepository.findAll();
    }

    public List<Block> getAccountHistory(String accountNumber) {
        return blockRepository.getBlocksByTransactionSenderAccountNumberEqualsOrTransactionReceiverAccountNumberEquals(accountNumber, accountNumber);
    }

    public List<Block> getAccountHistoryWithinPeriod(String accountNumber, Long start, Long end) {
        start--;
        end++;
        return blockRepository.getBlocksByTimestampBetweenAndTransactionSenderAccountNumberEqualsOrTransactionReceiverAccountNumberEquals(start, end, accountNumber, accountNumber);
    }

    public Block getTransactionInTime(String accountNumber, Long timestamp) {
        return blockRepository.getBlockByTimestampEqualsAndTransactionSenderAccountNumberEqualsOrTransactionReceiverAccountNumberEquals(timestamp, accountNumber, accountNumber);
    }

    public Block getLastTransaction(String accountNumber) {
        return blockRepository.getBlocksByTransactionSenderAccountNumberEqualsOrTransactionReceiverAccountNumberEqualsOrderByTimestampDesc(accountNumber, accountNumber).get(0);
    }

    public Block getLastBlock() {
        List<Block> blocks = getBlockchain();
        if (blocks.isEmpty()) {
            return null;
        }
        return blocks.get(blocks.size() - 1);
    }

}
