package com.example.ISOFTBlockchain.block;

import com.example.ISOFTBlockchain.transaction.Transaction;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.MessageDigest;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "blockchain")
public class Block {
    //private Integer blockID;
    private String hash;
    private String previousHash;
    private int nonce;
    private Long timestamp;
    private Transaction transaction;

    public Block(Transaction transaction, String previousHash) {
        this.transaction = transaction;
        this.timestamp = System.currentTimeMillis();
        this.previousHash = previousHash;
        this.nonce = 0;
        setHash();
    }

    public Block() {
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash() {
        String data = transaction.toString() + timestamp + previousHash + nonce;
        String generatedHash = calculateHash(data);

        this.hash = generatedHash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String calculateHash(String data) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hexadecimal = Integer.toHexString(0xff & b);
                if (hexadecimal.length() == 1) hexString.append('0');
                hexString.append(hexadecimal);
            }
            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public void incrementNonce() {
        this.nonce++;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public Integer getNonce() {
        return nonce;
    }

    @Override
    public String toString() {
        return "Block{" +
                ", timestamp=" + timestamp +
                ", blockHash='" + hash + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", nonce=" + nonce +
                ", transaction=" + transaction +
                '}';
    }
}