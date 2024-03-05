package com.example.ISOFTBlockchain.networking;

import com.example.ISOFTBlockchain.account.Account;
import com.example.ISOFTBlockchain.block.Block;
import com.example.ISOFTBlockchain.blockchain.BlockchainServices;
import com.example.ISOFTBlockchain.constants.Constants;
import com.example.ISOFTBlockchain.transaction.Transaction;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private Socket socket;
    private Account account;
    private Map<String, Account> accounts = new HashMap<>();
    private List<Block> blockchain;

    private boolean isMiner;

    public boolean isMiner() {
        return isMiner;
    }

    public void setMiner(boolean miner) {
        isMiner = miner;
    }

    public Client(Socket socket, Account account, List<Block> blockchain) {
        this.socket = socket;
        this.account = account;
        this.blockchain = blockchain;
    }

    public Account getAccount() {
        return account;
    }

    public boolean isHackedClient() {
        Account dbAccount = BlockchainServices.getAccount(account.getAccountNumber()) ;
        Account referenceAccount = accounts.get(account.getAccountNumber());

        return (!dbAccount.toString().equals(referenceAccount.toString()));
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(List<Block> blockchain) {
        this.blockchain = blockchain;
        updateLedger();
    }

    public void updateLedger() {
        if(!blockchain.isEmpty()) {
            List<Account> ledger = blockchain.get(blockchain.size() - 1).getTransaction().getLedger();
            for (Account account : ledger) {
                accounts.put(account.getAccountNumber(), account);
            }
        }
    }
    public boolean isValidTransaction(Transaction transaction) {
        Account sender = transaction.getSender();
        Account receiver = transaction.getReceiver();
        Double amount = transaction.getAmount();
        String transactionType = transaction.getTransactionType();

        if(transactionType.equals(Constants.ACCOUNT_CREATED)){
            while(accounts.get(sender.getAccountNumber())!=null) {
                sender.setAccountNumber(sender.generateAccountNumber(sender.getOwnerName()));
            }
        }

        return switch (transactionType) {
            case Constants.DEPOSIT -> amount > 0;
            case Constants.WITHDRAW -> ((accounts.get(sender.getAccountNumber()).getBalance() >= amount) && amount > 0);
            case Constants.ACCOUNT_CREATED -> (accounts.get(sender.getAccountNumber()) == null);
            default -> true;
        };
    }

    public boolean isValidBlockchain() {
        for(int i=1; i< blockchain.size(); i++) {
            Block currentBlock = blockchain.get(i-1);
            Block nextBlock = blockchain.get(i);

            String currentBlockHash = mine(currentBlock).getHash();
            String nextBlockPreviousHash = nextBlock.getPreviousHash();

            if (!nextBlockPreviousHash.equals(currentBlockHash)) return false;
        }
        return true;
    }

    public boolean isValidBlock(Block block) {
        Block referenceBlock = null;
        if (!blockchain.isEmpty()) {
            referenceBlock = blockchain.get(blockchain.size() - 1);
        }

        String blockHash = block.getHash();
        String blockPreviousHash = block.getPreviousHash();

        String previousHash = ((referenceBlock != null) ? referenceBlock.getHash() : Constants.GENESIS_PREV_HASH);
        Block blockTmp = new Block(block.getTransaction(), previousHash);
        blockTmp.setTimestamp(block.getTimestamp());

        String hash = mine(blockTmp).getHash();

        return (blockHash.equals(hash) && blockPreviousHash.equals(previousHash));
    }

    public Block mine(Block block) {
        String hash = block.getHash();

        while (!AcceptedHash(hash)) {
            block.setHash();
            block.incrementNonce();
            hash = block.getHash();
        }

        return block;
    }
    private boolean AcceptedHash(String hash) {
        char[] difficulty = new char[Constants.DIFFICULTY];
        String leadingZeros = new String(difficulty).replace('\0', '0');

        return (hash.substring(0, Constants.DIFFICULTY).equals(leadingZeros));
    }

    public void closeConnection() {
        try {
            if(socket!=null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
