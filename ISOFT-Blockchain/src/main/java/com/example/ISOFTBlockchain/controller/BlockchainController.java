package com.example.ISOFTBlockchain.controller;

import com.example.ISOFTBlockchain.account.Account;
import com.example.ISOFTBlockchain.block.Block;
import com.example.ISOFTBlockchain.blockchain.BlockchainServices;
import com.example.ISOFTBlockchain.transaction.Transaction;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/blockchain")
public class BlockchainController {

    private final BlockchainServices blockchainServices;

    public BlockchainController(BlockchainServices blockchainServices) {
        this.blockchainServices = blockchainServices;
    }

    @GetMapping("")
    public List<Block> getBlockchain() {
        return blockchainServices.getBlockchain();
    }

    @PostMapping("")
    public Object addAccount(@RequestBody Account account) {

        return blockchainServices.createAccount(account);
    }

    @GetMapping("/accounts/{accountNumber}")
    public Account getAccount(@PathVariable("accountNumber") String accountNumber) {
        return blockchainServices.getAccount(accountNumber);
    }

    @GetMapping("/accounts/{accountNumber}/history")
    public List<Transaction> getAccountHistory(@PathVariable("accountNumber") String accountNumber) {
        return blockchainServices.getAccountHistory(accountNumber);
    }

    @GetMapping("/accounts/{accountNumber}/history/last-transaction")
    public Transaction getLastTransaction(@PathVariable("accountNumber") String accountNumber) {
        return blockchainServices.getLastTransaction(accountNumber);
    }

    @GetMapping("/accounts/{accountNumber}/history/from/{start}/to/{end}")
    public List<Transaction> getTransactionsInPeriod(@PathVariable("accountNumber") String accountNumber,
                                          @PathVariable("start") Long start,
                                          @PathVariable("end") Long end) {
        return blockchainServices.getTransactionsInPeriod(accountNumber, start, end);
    }

    @PutMapping("/deposit/in/{accountNumber}/amount/{amount}")
    public Object deposit(@PathVariable("accountNumber") String accountNumber,
                         @PathVariable("amount") Double amount) {

        return blockchainServices.deposit(accountNumber, amount);

    }

    @PutMapping("/withdraw/from/{accountNumber}/amount/{amount}")
    public Object withdraw(@PathVariable("accountNumber") String accountNumber,
                          @PathVariable("amount") Double amount) {

        return blockchainServices.withdraw(accountNumber, amount);

    }

    @PutMapping("/transfer/from/{fromAccountNumber}/to/{toAccountNumber}/amount/{amount}")
    public Object transfer(@PathVariable("fromAccountNumber") String fromAccountNumber,
                          @PathVariable("toAccountNumber") String toAccountNumber,
                          @PathVariable("amount") Double amount) {

        return blockchainServices.transfer(fromAccountNumber, toAccountNumber, amount);
    }
}
