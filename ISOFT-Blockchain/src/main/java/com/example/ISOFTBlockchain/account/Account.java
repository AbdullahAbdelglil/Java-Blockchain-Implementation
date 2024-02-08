package com.example.ISOFTBlockchain.account;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.*;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection ="accounts")
@Data
@NoArgsConstructor
public class Account implements Comparable<Account> {

    @Id
    @JsonIgnore
    private String _id;

    private String accountNumber;

    private String ownerName;

    private Double balance;

    private Long creationDate;

    public Account(String ownerName, Double balance) {
        this.ownerName = ownerName;
        this.balance = balance;
        this.accountNumber = generateAccountNumber(ownerName);
        this.creationDate = System.currentTimeMillis();
    }

    public String generateAccountNumber(String owner) {

        StringBuilder accountNumber = new StringBuilder();

        accountNumber.append(owner.substring(0, 2).toUpperCase());
        accountNumber.append("00");

        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;

        accountNumber.append(randomNumber);
        accountNumber.append("0001");

        return accountNumber.toString();
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", balance=" + balance +
                ", creationDate=" + creationDate +
                '}';
    }

    @Override
    public int compareTo(Account account) {
        return Long.compare (this.creationDate, account.creationDate);
    }
}
