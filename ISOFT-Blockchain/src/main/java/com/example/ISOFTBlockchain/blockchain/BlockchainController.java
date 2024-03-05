package com.example.ISOFTBlockchain.blockchain;

import com.example.ISOFTBlockchain.account.Account;
import com.example.ISOFTBlockchain.block.Block;
import com.example.ISOFTBlockchain.transaction.Transaction;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/blockchain/accounts")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BlockchainController {

    private final BlockchainServices blockchainServices;

    public BlockchainController(BlockchainServices blockchainServices) {
        this.blockchainServices = blockchainServices;
    }

    @PostMapping("")
    public Object addAccount(@RequestBody Account account) {

        return blockchainServices.createAccount(account);
    }

    @GetMapping("/{accountNumber}/ledger")
    public List<Block> getBlockchain(@PathVariable("accountNumber") String accountNumber) {
        return BlockchainServices.getInMemoryBlockchain(accountNumber);
    }


    @GetMapping("/account/{accountNumber}")
    public Account getAccount(@PathVariable("accountNumber") String accountNumber) {
        return blockchainServices.getAccount(accountNumber);
    }

    @GetMapping("/{accountNumber}/history")
    public List<Block> getAccountHistory(@PathVariable("accountNumber") String accountNumber) {
        return blockchainServices.getAccountHistory(accountNumber);
    }

    @GetMapping("/{accountNumber}/history/last-transaction")
    public Block getLastTransaction(@PathVariable("accountNumber") String accountNumber) {
        return blockchainServices.getLastTransaction(accountNumber);
    }

    @GetMapping("/{accountNumber}/history/from/{start}/to/{end}")
    public List<Block> getTransactionsWithinPeriod(@PathVariable("accountNumber") String accountNumber,
                                                   @PathVariable("start") Long start,
                                                   @PathVariable("end") Long end) {
        return blockchainServices.getTransactionsInPeriod(accountNumber, start, end);
    }

    @GetMapping("/{accountNumber}/history/in/{time}")
    public Block getTransactionsInTime(@PathVariable("accountNumber") String accountNumber,
                                            @PathVariable("time") Long time) {
        return blockchainServices.getTransactionInTime(accountNumber, time);
    }

    @PutMapping("/{accountNumber}/deposit/{amount}")
    public Object deposit(@PathVariable("accountNumber") String accountNumber,
                         @PathVariable("amount") Double amount) {

        return blockchainServices.deposit(accountNumber, amount);

    }

    @PutMapping("/{accountNumber}/withdraw/{amount}")
    public Object withdraw(@PathVariable("accountNumber") String accountNumber,
                          @PathVariable("amount") Double amount) {

        return blockchainServices.withdraw(accountNumber, amount);

    }

    @PutMapping("/{accountNumber}/transfer/{amount}/to/{receiver}")
    public Object transfer(@PathVariable("accountNumber") String senderAccountNumber,
                          @PathVariable("receiver") String receiverAccountNumber,
                          @PathVariable("amount") Double amount) {

        return blockchainServices.transfer(senderAccountNumber, receiverAccountNumber, amount);
    }
}
