package com.example.ISOFTBlockchain.transaction;

import com.example.ISOFTBlockchain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@AllArgsConstructor
public class HackedTransaction extends Transaction {

    private String Error_Message;

    private Transaction Hacked_Transaction;

    private Transaction Correct_Transaction;
}
