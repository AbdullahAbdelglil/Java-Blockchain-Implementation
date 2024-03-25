package com.example.ISOFTBlockchain.block;

import com.example.ISOFTBlockchain.transaction.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class HackedTransaction extends Transaction {

    private Transaction hacked_transaction;
    private Transaction correct_transaction;

    public HackedTransaction(Transaction hacked_transaction, Transaction correct_transaction) {

        this.hacked_transaction = hacked_transaction;
        this.correct_transaction = correct_transaction;
    }

    public Transaction getHacked_transaction() {
        return hacked_transaction;
    }

    public void setHacked_transaction(Transaction hacked_transaction) {
        this.hacked_transaction = hacked_transaction;
    }

    public Transaction getCorrect_transaction() {
        return correct_transaction;
    }

    public void setCorrect_transaction(Transaction correct_transaction) {
        this.correct_transaction = correct_transaction;
    }
}
