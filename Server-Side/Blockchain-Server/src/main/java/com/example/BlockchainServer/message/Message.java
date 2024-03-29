package com.example.BlockchainServer.message;

public class Message {
    private boolean validTransaction;
    private boolean validBlock;
    private MessageType type;
    private String userName;
    private Double balance;
    private String content;
    private Object transaction;
    private Object block;
    private Object blockchain;

    public Object getBlock() {
        return block;
    }

    public void setBlock(Object block) {
        this.block = block;
    }

    public Object getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(Object blockchain) {
        this.blockchain = blockchain;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Object getTransaction() {
        return transaction;
    }

    public void setTransaction(Object transaction) {
        this.transaction = transaction;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isValidTransaction() {
        return validTransaction;
    }

    public void setValidTransaction(boolean validTransaction) {
        this.validTransaction = validTransaction;
    }

    public boolean isValidBlock() {
        return validBlock;
    }

    public void setValidBlock(boolean validBlock) {
        this.validBlock = validBlock;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
