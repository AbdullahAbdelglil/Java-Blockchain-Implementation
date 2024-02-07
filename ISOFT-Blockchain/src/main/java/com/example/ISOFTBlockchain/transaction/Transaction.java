package com.example.ISOFTBlockchain.transaction;

import com.example.ISOFTBlockchain.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
    private String transactionType;
    private Account sender;
    private Account receiver;
    private Double amount;
    private List<Account> ledger;
    @JsonIgnore
    private Long timeStamp;

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<Account> getLedger() {
        return ledger;
    }

    public void setLedger(List<Account> ledger) {
        this.ledger = ledger;
    }

    @Override
    public String toString() {
        int i=0;
        StringBuilder transactionData = new StringBuilder();
        transactionData.append(transactionType);
        transactionData.append(sender.toString());
        transactionData.append(amount);
        if(receiver != null) {
            transactionData.append(receiver.toString());
        }

        Collections.sort(ledger);
        for(Account account: ledger) {
            transactionData.append(account.toString());
        }

        return transactionData.toString();
    }
}
