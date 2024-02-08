package com.example.ISOFTBlockchain.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class HackedAccount extends Account {

    private String Error_Message;

    private Account Hacked_Account;

    private Account Correct_Account;
}
