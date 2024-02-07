package com.example.ISOFTBlockchain.account;

import com.example.ISOFTBlockchain.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private Map<String, List<Transaction>> accountsHistory;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account getAccount(String accountNumber) {
        return accountRepository.getAccountByAccountNumber(accountNumber);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void saveTransaction(String accountNumber, Transaction transaction) {
        if (accountsHistory == null) {
            accountsHistory = new HashMap<>();
        }

        List<Transaction> transactions = accountsHistory.get(accountNumber);
        if (transactions == null) {
            transactions = new ArrayList<>();
        }

        transactions.add(transaction);
        accountsHistory.put(accountNumber, transactions);
    }
    public List<Transaction> getAccountHistory(String accountNumber) {
        return accountsHistory.get(accountNumber);
    }

    public List<Transaction> getTransactionInPeriod(String accountNumber, Long start, Long end) {
        List<Transaction> history = accountsHistory.get(accountNumber);
        List<Transaction> transactions = new ArrayList<>();

        for(Transaction transaction: history) {
            if(transaction.getTimeStamp()>= start && transaction.getTimeStamp()<= end) {
                transactions.add(transaction);
            }
        }

        return transactions;
    }

    public Transaction getLastTransaction(String accountNumber) {
        List<Transaction> history = accountsHistory.get(accountNumber);

        if(history==null) {
            return null;
        }

        return history.get(history.size()-1);
    }

}
