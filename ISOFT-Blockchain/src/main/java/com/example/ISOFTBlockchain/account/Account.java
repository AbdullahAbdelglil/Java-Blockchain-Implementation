package com.example.ISOFTBlockchain.account;

import com.example.ISOFTBlockchain.block.Block;
import com.example.ISOFTBlockchain.blockchain.BlockchainServices;
import com.example.ISOFTBlockchain.constants.Constants;
import com.example.ISOFTBlockchain.transaction.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDate;

import java.util.*;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection ="accounts")
public class Account implements Comparable<Account> {

    @Id
    @JsonIgnore
    private String _id;
    private String accountNumber;
    private String ownerName;
    private Double balance;
    private LocalDate creationDate;

    public Account() {
    }

    public Account(String ownerName, Double balance) {
        this.ownerName = ownerName;
        this.balance = balance;
        this.accountNumber = generateAccountNumber(ownerName);
        this.creationDate = LocalDate.now();
    }

    public String generateAccountNumber(String owner) {

        StringBuilder accountNumber = new StringBuilder();

        accountNumber.append(owner.substring(0, 2).toUpperCase());
        accountNumber.append("00");

        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;

        accountNumber.append(randomNumber);
        accountNumber.append("0001");

        return accountNumber.toString();
    }
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", balance=" + balance +
                ", creationDate=" + creationDate +
                '}';
    }

    @Override
    public int compareTo(Account account) {
        return Double.compare(this.balance, account.balance);
    }
}
