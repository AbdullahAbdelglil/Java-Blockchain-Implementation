package com.example.ISOFTBlockchain.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HackedTransaction{
    private String Error_Message;
    private Transaction Hacked_Transaction;
    private Transaction Correct_Transaction;

    public HackedTransaction() {
    }

    public HackedTransaction(String error_Message, Transaction hacked_Transaction, Transaction correct_Transaction) {
        Error_Message = error_Message;
        Hacked_Transaction = hacked_Transaction;
        Correct_Transaction = correct_Transaction;
    }

    public String getError_Message() {
        return Error_Message;
    }

    public void setError_Message(String error_Message) {
        Error_Message = error_Message;
    }

    public Transaction getHacked_Transaction() {
        return Hacked_Transaction;
    }

    public void setHacked_Transaction(Transaction hacked_Transaction) {
        Hacked_Transaction = hacked_Transaction;
    }

    public Transaction getCorrect_Transaction() {
        return Correct_Transaction;
    }

    public void setCorrect_Transaction(Transaction correct_Transaction) {
        Correct_Transaction = correct_Transaction;
    }


}
