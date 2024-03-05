package com.example.ISOFTBlockchain.account;



public class AccountNotFound extends Account{
    private String Error_Message;

    public AccountNotFound(String error_Message) {
        Error_Message = error_Message;
    }

    public String getError_Message() {
        return Error_Message;
    }

    public void setError_Message(String error_Message) {
        Error_Message = error_Message;
    }
}
