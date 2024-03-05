package com.example.ISOFTBlockchain.blockchain;

import com.example.ISOFTBlockchain.account.Account;
import com.example.ISOFTBlockchain.account.AccountNotFound;
import com.example.ISOFTBlockchain.account.AccountService;
import com.example.ISOFTBlockchain.account.HackedAccount;
import com.example.ISOFTBlockchain.block.Block;
import com.example.ISOFTBlockchain.block.BlockService;
import com.example.ISOFTBlockchain.networking.Client;
import com.example.ISOFTBlockchain.transaction.HackedTransaction;
import com.example.ISOFTBlockchain.constants.Constants;
import com.example.ISOFTBlockchain.transaction.InvalidTransaction;
import com.example.ISOFTBlockchain.transaction.Transaction;
import com.hazelcast.collection.IList;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

@Service
public class BlockchainServices {
    private static AccountService accountService;
    private static BlockService blockService ;
    private static final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();
    private static final IList<Block> IBlockchain = hazelcast.getList("blockchain");
    public static final Map<String, Client> clients = new HashMap<>();
    public static final List<Client> miners = new ArrayList<>();

    //-------------------------------------------------Constructor------------------------------------------------------

    public BlockchainServices(AccountService accService, BlockService blockService) {
        BlockchainServices.accountService = accService;
        BlockchainServices.blockService = blockService;
    }

    //----------------------------------------------------Init----------------------------------------------------------

    public static void init() {
        Block lastBlock = blockService.getLastBlock();
        if (lastBlock != null ) {
            IBlockchain.add(lastBlock);
        }
        List<Account> offlineClients = lastBlock.getTransaction().getLedger();
        for(Account account:offlineClients) {
            Client client = createClient(account);
            persistClient(client);
            client.updateLedger();
        }
    }

    //----------------------------------------------Account Services----------------------------------------------------

    public Object createAccount(Account acc) {
        Account account = new Account(acc.getOwnerName(), acc.getBalance());

        Transaction transaction = buildTransaction(Constants.ACCOUNT_CREATED, account, null, account.getBalance());
        boolean validTransaction = isValidTransaction(transaction);

        if(validTransaction) {
            Block block = new Block(transaction, getLastHash());
            Client client = createClient(account);
            Client miner = ((miners.isEmpty())?client:getTheMiner());

            block = miner.mine(block);

            if(block.getPreviousHash().equals(Constants.GENESIS_PREV_HASH) && isValidBlock(block)) {
                printTheBlockMakerInfo(miner);
                persistClient(client);
                return recordTransaction(block, account, null, transaction);
            }
            else {
                if (isValidBlock(block) && (isValidBlockchain() instanceof Boolean)) {
                    persistClient(client);
                    printTheBlockMakerInfo(miner);
                    return recordTransaction(block, account, null, transaction);
                }
                else {
                    return isValidBlockchain();
                }
            }
        }
        return new InvalidTransaction("Invalid Transaction");
    }

    private static Client createClient(Account account) {
        try {
            Socket socket = new Socket("localhost",2024);
            return new Client(socket, account, IBlockchain);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void persistClient(Client client) {
        String accountNumber = client.getAccount().getAccountNumber();
        System.out.println(
                "\n--------------------------\n"+
                        accountNumber+" Active now !"+
                        "\n--------------------------\n");

        clients.put(accountNumber, client);
        if(miners.size()<Constants.MINERS_LIMIT) {
            miners.add(client);
            client.setMiner(true);
        }
    }

    public Object withdraw(String accountNumber, Double amount) {
        Account dbAccount = accountService.getAccount(accountNumber);

        if (dbAccount == null) {
            return new AccountNotFound(Constants.ACCOUNT_NOT_FOUND);
        }

        Transaction transaction = buildTransaction(Constants.WITHDRAW, dbAccount, null, amount);
        boolean validTransaction = isValidTransaction(transaction);

        if (validTransaction) {

            Double balance = dbAccount.getBalance();
            balance -= amount;
            dbAccount.setBalance(balance);

            Block block = new Block(transaction, getLastHash());
            Client client = clients.get(accountNumber);
            Client miner = ((client.isMiner()) ? client : getTheMiner());
            block = miner.mine(block);
            printTheBlockMakerInfo(miner);

            if (isValidBlock(block) && (isValidBlockchain() instanceof Boolean)) {
                return recordTransaction(block, dbAccount, null, transaction);
            } else {
                return isValidBlockchain();
            }
        } else {
            return new InvalidTransaction(Constants.SUFFICIENT_FUNDS);
        }
    }

    public Object deposit(String accountNumber, Double amount) {
        Account dbAccount = accountService.getAccount(accountNumber);

        if (dbAccount == null) {
            return new AccountNotFound(Constants.ACCOUNT_NOT_FOUND);
        }

        Transaction transaction = buildTransaction(Constants.DEPOSIT, dbAccount, null, amount);
        boolean validTransaction = isValidTransaction(transaction);

        if(validTransaction) {

            Double balance = dbAccount.getBalance();
            balance += amount;
            dbAccount.setBalance(balance);

            Block block = new Block(transaction, getLastHash());
            Client client = clients.get(accountNumber);
            Client miner = ((client.isMiner())?client:getTheMiner());
            block  = miner.mine(block);
            printTheBlockMakerInfo(miner);

            if (isValidBlock(block) && (isValidBlockchain() instanceof Boolean)) {
                return recordTransaction(block, dbAccount, null, transaction);
            } else {
                return isValidBlockchain();
            }
        }
        else {
            return new InvalidTransaction(Constants.SUFFICIENT_FUNDS);
        }
    }

    public Object transfer(String fromAccountNumber, String toAccountNumber, Double amount) {
        Account sender = accountService.getAccount(fromAccountNumber);
        Account receiver = accountService.getAccount(toAccountNumber);

        if (sender == null || receiver == null) {
            return new AccountNotFound(Constants.ACCOUNT_NOT_FOUND);
        }

        Transaction transaction = buildTransaction(Constants.TRANSFER, sender, receiver, amount);
        boolean validTransaction = isValidTransaction(transaction);

        if (validTransaction) {

            Double senderBalance = sender.getBalance();
            Double receiverBalance = receiver.getBalance();

            senderBalance -= amount;
            receiverBalance += amount;
            sender.setBalance(senderBalance);
            receiver.setBalance(receiverBalance);

            Client senderClient = clients.get(fromAccountNumber);
            Client recieverClient = clients.get(toAccountNumber);
            Client miner;

            if (senderClient.isMiner() && recieverClient.isMiner()) {
                miner = ((senderBalance + amount > receiverBalance - amount) ? senderClient : recieverClient);
            } else if (senderClient.isHackedClient() || receiver.isMiner()) {
                miner = ((senderClient.isMiner()) ? senderClient : recieverClient);
            } else {
                miner = getTheMiner();
            }

            Block block = new Block(transaction, getLastHash());
            block = miner.mine(block);
            printTheBlockMakerInfo(miner);

            if (isValidBlock(block) && (isValidBlockchain() instanceof Boolean)) {
                return recordTransaction(block, sender, receiver, transaction);
            } else {
                return isValidBlockchain();
            }
        } else {
            return new InvalidTransaction(Constants.SUFFICIENT_FUNDS);
        }
    }

    private Client getTheMiner() {
        Client miner = miners.get(0);
        for(int i=1; i<miners.size(); i++) {
            Client currentMiner = miners.get(i);
            if(miner.getAccount().getBalance() < currentMiner.getAccount().getBalance()) {
                miner = currentMiner;
            }
        }
        return miner;
    }

    private void printTheBlockMakerInfo(Client miner) {
        System.out.println("\n--------------------------\n"+
                "Block Maker: "+ miner.getAccount().getAccountNumber()+
                "\n--------------------------\n");
    }

    //------------------------------------------Account Services--------------------------------------------------------
    public static Account getAccount(String accountNumber) {
        Account dbAccount = accountService.getAccount(accountNumber);
        if (dbAccount == null) {
            return new AccountNotFound(Constants.ACCOUNT_NOT_FOUND);
        }

        return dbAccount;
    }

    public List<Block> getAccountHistory(String accountNumber) {
        return blockService.getAccountHistory(accountNumber);
    }

    public Block getLastTransaction(String accountNumber) {
        return blockService.getLastTransaction(accountNumber);
    }

    public List<Block> getTransactionsInPeriod(String accountNumber, Long start, Long end) {
        if(start.equals(end)) {
            List<Block> blocks = new ArrayList<>();
            blocks.add(blockService.getTransactionInTime(accountNumber, start));
            return blocks;
        }
        return blockService.getAccountHistoryWithinPeriod(accountNumber, start, end);
    }

    public Block getTransactionInTime(String accountNumber, Long timeStamp) {
        return blockService.getTransactionInTime(accountNumber, timeStamp);
    }

    //------------------------------------------------------------------------------------------------------------------

    public Block recordTransaction(Block block, Account sender, Account receiver, Transaction transaction) {
        transaction.setTimeStamp(block.getTimestamp());
        IBlockchain.add(block);
        broadcastBlock();
        accountService.save(sender);
        if (receiver != null) accountService.save(receiver);
        blockService.save(block);
        return block;

    }

    public void broadcastBlock() {
        for (Client client : clients.values()) {
            client.setBlockchain(IBlockchain);
        }
    }

    //----------------------------------------------Blockchain Services-------------------------------------------------

    public static List<Block> getInMemoryBlockchain(String accountNumber) {
        return clients.get(accountNumber).getBlockchain();
    }

    //---------------------------------------------Helper Methods-------------------------------------------------------

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

    public boolean isValidTransaction(Transaction transaction) {
        if (clients.isEmpty()) return true;

        int counter = 0;
        int clientsSize = clients.size();

        System.out.println(transaction.getTransactionType()+"\n--------------------------\n");
        for (Client client : clients.values()) {
            if(client.isHackedClient()) {
                System.out.println(client.getAccount().getAccountNumber() + ": Im Hacked :(");
                clientsSize--;
            }
            else {
                if (client.isValidTransaction(transaction)) {
                    System.out.println(client.getAccount().getAccountNumber() + ": ✔ Valid Transaction");
                    counter++;
                } else {
                    System.out.println(client.getAccount().getAccountNumber() + ": ❌ Invalid Transaction");
                }
            }
        }
        System.out.println("\n--------------------------\n");

        return (counter == clientsSize);
    }

    public boolean isValidBlock(Block block) {
        if(miners.isEmpty()) return true;

        int counter = 0;
        int minersSize = miners.size();

        System.out.println("\n--------------------------\n");
        for (Client miner : miners) {
            if(miner.isHackedClient()) {
                System.out.println(miner.getAccount().getAccountNumber() + ": Im Hacked :(");
                minersSize--;
            }
            else {
                if (miner.isValidBlockchain() && miner.isValidBlock(block)) {
                    System.out.println(miner.getAccount().getAccountNumber() + ": ✔ Valid Block");
                    counter++;
                } else {
                    System.out.println(miner.getAccount().getAccountNumber() + ": ❌ Invalid Block");
                }
            }
        }
        System.out.println("\n--------------------------\n");

        return  (counter==minersSize);
    }

    public String getLastHash() {
        Block lastBlock = blockService.getLastBlock();
        String lastBlockHash = ((lastBlock == null) ? Constants.GENESIS_PREV_HASH : lastBlock.getHash());
        return lastBlockHash;
    }



    public Object isValidBlockchain() {
        List<HackedAccount> accountsTest = verifyAccounts();
        Object blockchainTest = verifyBlockchain();

        if (blockchainTest == null && accountsTest == null) return true;
        else if (accountsTest != null && blockchainTest == null) return accountsTest;
        else if (accountsTest == null && blockchainTest != null) return blockchainTest;
        else return accountsTest;
    }

    public List<HackedAccount> verifyAccounts() {
        List<Account> dbAccounts = accountService.getAllAccounts();
        List<Account> memoryAccounts = IBlockchain.get(IBlockchain.size() - 1).getTransaction().getLedger() ;

        Collections.sort(dbAccounts);
        Collections.sort(memoryAccounts);

        List<HackedAccount> hackedAccounts = getAccountsHacked(memoryAccounts, dbAccounts);
        if (hackedAccounts.isEmpty()) return null;
        return hackedAccounts;
    }

    private static List<HackedAccount> getAccountsHacked(List<Account> memoryAccounts, List<Account> dbAccounts) {
        List<HackedAccount> hackedAccounts = new ArrayList<>();

        for (int i = 0; i < memoryAccounts.size(); i++) {
            Account dbAccount = dbAccounts.get(i);
            Account memoryAccount = memoryAccounts.get(i);
            HackedAccount hackedAccount;
            if (!(dbAccount.toString().equals(memoryAccount.toString()))) {
                hackedAccount = new HackedAccount();
                hackedAccount.setError_Message(Constants.ACCOUNTS_DB_HACKED_ERROR);
                hackedAccount.setHacked_Account(dbAccount);
                hackedAccount.setCorrect_Account(memoryAccount);
                hackedAccounts.add(hackedAccount);
            }
        }
        return hackedAccounts;
    }

    public Object verifyBlockchain() {
        Transaction lastTransactionDB = blockService.getLastBlock().getTransaction();
        Transaction lastTransactionMemory;

        lastTransactionMemory = IBlockchain.get(IBlockchain.size() - 1).getTransaction();

        if (!lastTransactionMemory.toString().equals(lastTransactionDB.toString())) {
            lastTransactionDB.setLedger(getHackedAccounts(lastTransactionDB.getLedger(), lastTransactionMemory.getLedger()));
            HackedTransaction hackedTransaction = new HackedTransaction();
            hackedTransaction.setError_Message(Constants.TRANSACTION_HACKED_ERROR);
            hackedTransaction.setHacked_Transaction(lastTransactionDB);
            hackedTransaction.setCorrect_Transaction(lastTransactionMemory);
            return hackedTransaction;
        }
        return null;
    }

    public static List<Account> getLastUpdatedLedger() {
        if (blockService.getLastBlock() != null) {
            return blockService.getLastBlock().getTransaction().getLedger();
        }
        return null;
    }

    public List<Account> getHackedAccounts(List<Account> hackedLedger, List<Account> correctLedger) {
        Collections.sort(hackedLedger);
        Collections.sort(correctLedger);
        List<Account> updatedLedger = new ArrayList<>();

        for(int i=0; i<correctLedger.size(); i++) {
            Account dbAccount = hackedLedger.get(i);
            Account memoAccount = correctLedger.get(i);
            if(!(dbAccount.toString().equals(memoAccount.toString()))) {
                dbAccount.setHacked(true);
            }

            updatedLedger.add(dbAccount);
        }
        return updatedLedger;
    }
}
