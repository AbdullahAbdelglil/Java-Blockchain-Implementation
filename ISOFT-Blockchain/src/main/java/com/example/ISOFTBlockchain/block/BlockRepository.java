package com.example.ISOFTBlockchain.block;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlockRepository extends MongoRepository<Block, String> {
    List<Block> getBlocksByTransactionSenderAccountNumberIsOrTransactionReceiverAccountNumberIsAndTimestampBetween(String accountNumber,String account_Number, Long start, Long end);

    List<Block> getBlocksByTransactionSenderAccountNumberIsOrTransactionReceiverAccountNumberIs(String accountNumber, String account_Number);

    Block getBlocksByTransactionSenderAccountNumberAndTimestampIs(String accountNumber, Long timestamp);

    List<Block> getBlocksByTransactionSenderAccountNumberIsOrTransactionReceiverAccountNumberIsOrderByTimestampDesc(String accountNumber, String account_Number);
}
