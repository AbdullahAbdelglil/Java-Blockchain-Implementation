package com.example.ISOFTBlockchain.transaction;

public class InvalidTransaction {
    private String Error_Message;
    public InvalidTransaction() {
    }

    public InvalidTransaction(String error_Message) {
        Error_Message = error_Message;
    }

    public String getError_Message() {
        return Error_Message;
    }

    public void setError_Message(String error_Message) {
        Error_Message = error_Message;
    }
}
