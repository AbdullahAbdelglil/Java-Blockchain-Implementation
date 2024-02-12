package com.example.ISOFTBlockchain.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HackedAccount {

    private String Error_Message;

    private Account Hacked_Account;

    private Account Correct_Account;

    public HackedAccount() {
    }

    public HackedAccount(String error_Message, Account hacked_Account, Account correct_Account) {
        this.Error_Message = error_Message;
        this.Hacked_Account = hacked_Account;
        this.Correct_Account = correct_Account;
    }

    public String getError_Message() {
        return Error_Message;
    }

    public void setError_Message(String error_Message) {
        Error_Message = error_Message;
    }

    public Account getHacked_Account() {
        return Hacked_Account;
    }

    public void setHacked_Account(Account hacked_Account) {
        Hacked_Account = hacked_Account;
    }

    public Account getCorrect_Account() {
        return Correct_Account;
    }

    public void setCorrect_Account(Account correct_Account) {
        Correct_Account = correct_Account;
    }
}
