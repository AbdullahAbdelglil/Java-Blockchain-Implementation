package com.example.ISOFTBlockchain.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HackedTransaction{
    private String errorMessage;
    private Transaction hackedTransaction;
    private Transaction correctTransaction;

    public HackedTransaction() {
    }

    public HackedTransaction(String error_Message, Transaction hacked_Transaction, Transaction correct_Transaction) {
        errorMessage = error_Message;
        hackedTransaction = hacked_Transaction;
        correctTransaction = correct_Transaction;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Transaction getHackedTransaction() {
        return hackedTransaction;
    }

    public void setHackedTransaction(Transaction hackedTransaction) {
        this.hackedTransaction = hackedTransaction;
    }

    public Transaction getCorrectTransaction() {
        return correctTransaction;
    }

    public void setCorrectTransaction(Transaction correctTransaction) {
        this.correctTransaction = correctTransaction;
    }


}
