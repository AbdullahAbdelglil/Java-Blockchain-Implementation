package com.example.ISOFTBlockchain.blockchain;

import com.example.ISOFTBlockchain.account.Account;
import com.example.ISOFTBlockchain.account.AccountNotFound;
import com.example.ISOFTBlockchain.account.AccountService;
import com.example.ISOFTBlockchain.account.HackedAccount;
import com.example.ISOFTBlockchain.block.Block;
import com.example.ISOFTBlockchain.block.BlockService;
import com.example.ISOFTBlockchain.transaction.HackedTransaction;
import com.example.ISOFTBlockchain.constants.Constants;
import com.example.ISOFTBlockchain.transaction.InvalidTransaction;
import com.example.ISOFTBlockchain.transaction.Transaction;
import com.hazelcast.collection.IList;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BlockchainServices {
    private final AccountService accountService;
    private final BlockService blockService;
    private final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();
    private IList<Block> IBlockchain = hazelcast.getList("blockchain");
    private IMap<String, Account> accounts = hazelcast.getMap("accounts");

    //-------------------------------------------------Constructor------------------------------------------------------

    public BlockchainServices(AccountService accountService, BlockService blockService) {
        this.accountService = accountService;
        this.blockService = blockService;
    }

    //----------------------------------------------Account Services----------------------------------------------------

    public Object createAccount(Account acc) {

        Account account = new Account(acc.getOwnerName(), acc.getBalance());
        Transaction transaction = buildTransaction(Constants.ACCOUNT_CREATED, account, null, account.getBalance());

        Block block = new Block(transaction, getLastHash());
        mine(block);

        if (isValidBlockchain() == (Boolean) true) {
            return recordTransaction(block, account, null, transaction);
        }

        return isValidBlockchain();
    }

    public Object withdraw(String accountNumber, Double amount) {
        Account dbAccount = accountService.getAccount(accountNumber);

        if(dbAccount==null) {
            return new AccountNotFound(Constants.ACCOUNT_NOT_FOUND);
        }

        Double balance = dbAccount.getBalance();

        if (balance < amount || amount < 0) {
            return new InvalidTransaction(Constants.SUFFICIENT_FUNDS);
        }

        balance -= amount;
        dbAccount.setBalance(balance);
        Transaction transaction = buildTransaction(Constants.WITHDRAW, dbAccount, null, amount);

        Block block = new Block(transaction, getLastHash());
        mine(block);

        if (isValidBlockchain() == (Boolean) true) {
            return recordTransaction(block, dbAccount, null, transaction);
        }
        return isValidBlockchain();
    }

    public Object deposit(String accountNumber, Double amount) {
        Account dbAccount = accountService.getAccount(accountNumber);

        if(dbAccount==null) {
            return new AccountNotFound(Constants.ACCOUNT_NOT_FOUND);
        }

        Double balance = dbAccount.getBalance();

        if (amount < 0) {
            return new InvalidTransaction(Constants.SUFFICIENT_FUNDS);
        }

        balance += amount;
        dbAccount.setBalance(balance);
        Transaction transaction = buildTransaction(Constants.DEPOSIT, dbAccount, null, amount);

        Block block = new Block(transaction, getLastHash());
        mine(block);

        if (isValidBlockchain() == (Boolean) true) {
            return recordTransaction(block, dbAccount, null, transaction);
        }
        return isValidBlockchain();
    }

    public Object transfer(String fromAccountNumber, String toAccountNumber, Double amount) {
        Account sender = accountService.getAccount(fromAccountNumber);
        Account receiver = accountService.getAccount(toAccountNumber);

        if(sender==null || receiver==null) {
            return new AccountNotFound(Constants.ACCOUNT_NOT_FOUND);
        }

        Double senderBalance = sender.getBalance();
        Double receiverBalance = receiver.getBalance();

        if (senderBalance < amount || amount < 0) {
            return new InvalidTransaction(Constants.SUFFICIENT_FUNDS);
        }

        senderBalance -= amount;
        receiverBalance += amount;
        sender.setBalance(senderBalance);
        receiver.setBalance(receiverBalance);
        Transaction transaction = buildTransaction(Constants.TRANSFER, sender, receiver, amount);

        Block block = new Block(transaction, getLastHash());
        mine(block);

        if (isValidBlockchain() == (Boolean) true) {
            return recordTransaction(block, sender, receiver, transaction);
        }
        return isValidBlockchain();
    }

    public Account getAccount(String accountNumber) {
        Account dbaccount = accountService.getAccount(accountNumber);
        if(dbaccount==null) {
            return new AccountNotFound(Constants.ACCOUNT_NOT_FOUND);
        }

        return dbaccount;
    }

    public List<Transaction> getAccountHistory(String accountNumber) {
        Account dbAccount = getAccount(accountNumber);
        return accountService.getAccountHistory(accountNumber);
    }

    public Transaction getLastTransaction(String accountNumber) {
        getAccount(accountNumber);
        return accountService.getLastTransaction(accountNumber);
    }

    public List<Transaction> getTransactionsInPeriod(String accountNumber, Long start, Long end) {
        getAccount(accountNumber);
        return accountService.getAccountHistoryInPeriod(accountNumber, start, end);
    }

    public Block recordTransaction(Block block, Account sender, Account receiver, Transaction transaction) {
        accountService.save(sender);
        accountService.saveTransaction(sender.getAccountNumber(), transaction);
        accounts.put(sender.getAccountNumber(), sender);

        if (receiver != null) {
            accountService.save(receiver);
            accountService.saveTransaction(receiver.getAccountNumber(), transaction);
            accounts.put(receiver.getAccountNumber(), receiver);
        }

        transaction.setTimeStamp(block.getTimestamp());
        IBlockchain.add(block);
        blockService.save(block);
        return block;
    }

    //----------------------------------------------Blockchain Services-------------------------------------------------

    public List<Block> getInMemoryBlockchain() {
        return IBlockchain;
    }

    public List<Block> getHistoricalBlockchain() {
        return blockService.getBlockchain();
    }

    //---------------------------------------------Helper Methods-------------------------------------------------------

    public void mine(Block block) {
        String hash = block.getHash();

        while (!AcceptedHash(hash)) {
            block.setHash();
            block.incrementNonce();
            hash = block.getHash();
        }

    }

    private boolean AcceptedHash(String hash) {
        char[] difficulty = new char[Constants.DIFFICULTY];
        String leadingZeros = new String(difficulty).replace('\0', '0');

        return (hash.substring(0, Constants.DIFFICULTY).equals(leadingZeros));
    }

    private Transaction buildTransaction(String transactionType, Account sender, Account receiver, Double amount) {
        Transaction transaction = new Transaction();

        transaction.setTransactionType(transactionType);
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);

        List<Account> lastLedger = accountService.getAllAccounts();

        if (lastLedger.isEmpty()) {
            lastLedger.add(sender);
            if (receiver != null) {
                lastLedger.add(receiver);
            }
        } else {
            Map<String, Account> accountMap = new HashMap<>();
            for (Account account : lastLedger) {
                accountMap.put(account.getAccountNumber(), account);
            }

            accountMap.put(sender.getAccountNumber(), sender);
            if (receiver != null) {
                accountMap.put(receiver.getAccountNumber(), receiver);
            }

            lastLedger = new ArrayList<>(accountMap.values());
        }
        transaction.setLedger(lastLedger);
        return transaction;
    }

    public String getLastHash() {
        Block lastBlock = blockService.getLastBlock();
        String lastBlockHash = ((lastBlock == null) ? Constants.GENESIS_PREV_HASH : lastBlock.getHash());
        return lastBlockHash;
    }

    public Object isValidBlockchain() {
        //System.out.println(hazelcast.getLifecycleService().isRunning());
        Transaction blockchainTest = verifyBlockchain();
        Account accountsTest = verifyAccounts();

        if (blockchainTest == null && accountsTest == null) return true;
        else if (accountsTest != null && blockchainTest == null) return accountsTest;
        else if (accountsTest == null && blockchainTest != null) return blockchainTest;
        else return null;
    }

    public Account verifyAccounts() {
        List<Account> dbAccounts = accountService.getAllAccounts();
        List<Account> memoryAccounts = new ArrayList<>(accounts.values());

        Collections.sort(dbAccounts);
        Collections.sort(memoryAccounts);

        for (int i = 0; i < dbAccounts.size(); i++) {
            Account dbAccount = dbAccounts.get(i);
            Account memoryAccount = memoryAccounts.get(i);

            if (!(dbAccount.toString().equals(memoryAccount.toString()))) {
                return new HackedAccount(Constants.DB_HACKED_ERROR, dbAccount, memoryAccount);
            }
        }
        return null;
    }

    public Transaction verifyBlockchain() {
        List<Block> dbBlockchain = blockService.getBlockchain();

        for (int i = 0; i < dbBlockchain.size(); i++) {
            Block dbBlock = dbBlockchain.get(i);
            Block memoryBlock = IBlockchain.get(i);

            if (!(dbBlock.toString().equals(memoryBlock.toString()))) {
                return new HackedTransaction(Constants.DB_HACKED_ERROR, dbBlock.getTransaction(), memoryBlock.getTransaction());
            }
        }
        return null;
    }


}

//    public String calculateLastBlockHash(Block block) {
//        int last = IBlockchain.size() - 1;
//        Block lastBlock = IBlockchain.get(last);
//        Block updatedBlock = new Block();
//
//        System.out.println("hash-1: " + lastBlock.getHash());
//
//        Transaction lastTransaction = lastBlock.getTransaction();
//        System.out.println("trans-1:" + lastTransaction);
//        List<Account> dbLedger = (accountService.getAllAccounts());
//        lastTransaction.setLedger(dbLedger);
//
//        updatedBlock.setTransaction(lastTransaction);
//        updatedBlock.setTimestamp(lastBlock.getTimestamp());
//        updatedBlock.setPreviousHash(lastBlock.getPreviousHash());
//        updatedBlock.setNonce(0);
//        updatedBlock.setHash();
//        String hash = mine(updatedBlock);
//
//        System.out.println("hash-2: " + hash);
//        System.out.println("trans-2:" + lastTransaction);
//        return hash;
//    }