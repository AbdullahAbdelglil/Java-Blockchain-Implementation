package com.example.ISOFTBlockchain.account;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
@Document(collection ="accounts")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Account implements Comparable<Account> {

    @Id
    @JsonIgnore
    private String _id;
    private String accountNumber;
    private String ownerName;
    private Double balance;
    private Long creationDate;
    private boolean hacked;

    public Account() {
    }

    public Account(String ownerName, Double balance) {
        this.ownerName = ownerName;
        this.balance = balance;
        this.accountNumber = generateAccountNumber(ownerName);
        this.creationDate = System.currentTimeMillis();
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

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }


    public boolean isHacked() {
        return hacked;
    }

    public void setHacked(Boolean hacked) {
        this.hacked = hacked;
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
        return Long.compare (this.creationDate, account.creationDate);
    }
}
