package com.example.ISOFTBlockchain.constants;

public class Constants {

    private Constants() {
    }

    public static final int DIFFICULTY = 4;
    public static final String GENESIS_PREV_HASH = "0000000000000000000000000000000000000000";
    public static final String ACCOUNT_CREATED = "Account Created Transaction";
    public static final String WITHDRAW = "Withdrawal Transaction";
    public static final String DEPOSIT = "Deposit Transaction";
    public static final String TRANSFER = "Transfer Transaction";
    public static final String DB_HACKED_ERROR = "Unfortunately, The transaction didn't complete, the Database Hacked";
    public static final String ACCOUNT_NOT_FOUND = "Account not found, please check your inputs and try again";
    public static final String SUFFICIENT_FUNDS = "Sufficient funds !, please check your balance and try again, or check that the amount is greater than zero";
}
