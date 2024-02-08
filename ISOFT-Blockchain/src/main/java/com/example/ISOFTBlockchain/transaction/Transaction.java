package com.example.ISOFTBlockchain.transaction;

import com.example.ISOFTBlockchain.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    private String transactionType;

    private Account sender;

    private Account receiver;

    private Double amount;

    private List<Account> ledger;

    @JsonIgnore
    private Long timeStamp;


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

        //For Correct Hashing
        Collections.sort(ledger);
        for(Account account: ledger) {
            transactionData.append(account.toString());
        }

        return transactionData.toString();
    }
}
