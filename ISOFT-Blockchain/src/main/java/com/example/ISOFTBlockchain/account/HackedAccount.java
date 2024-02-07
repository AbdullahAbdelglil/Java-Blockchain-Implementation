package com.example.ISOFTBlockchain.account;

public class HackedAccount extends Account {

    private Account hacked_account;
    private Account correct_account;

    public HackedAccount(Account hackedAccount, Account correctAccount) {
        this.hacked_account = hackedAccount;
        this.correct_account = correctAccount;
    }

    public HackedAccount(String ownerName, Double balance) {
        super(ownerName, balance);
    }

    public Account getHacked_account() {
        return hacked_account;
    }

    public void setHacked_account(Account hacked_account) {
        this.hacked_account = hacked_account;
    }

    public Account getCorrect_account() {
        return correct_account;
    }

    public void setCorrect_account(Account correct_account) {
        this.correct_account = correct_account;
    }
}
