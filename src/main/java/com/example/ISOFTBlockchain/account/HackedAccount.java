package com.example.ISOFTBlockchain.account;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HackedAccount {



    private Account hackedAccount;

    private Account correctAccount;

    public HackedAccount() {
    }

    public HackedAccount(Account hacked_Account, Account correct_Account) {
        this.hackedAccount = hacked_Account;
        this.correctAccount = correct_Account;
    }

    public Account getHackedAccount() {
        return hackedAccount;
    }

    public void setHackedAccount(Account hackedAccount) {
        this.hackedAccount = hackedAccount;
    }

    public Account getCorrectAccount() {
        return correctAccount;
    }

    public void setCorrectAccount(Account correctAccount) {
        this.correctAccount = correctAccount;
    }
}
