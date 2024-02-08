package com.example.ISOFTBlockchain.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AccountNotFound extends Account{
    private String Error_Message;
}
