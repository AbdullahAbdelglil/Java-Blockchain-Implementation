package com.example.ISOFTBlockchain.blockchain;

import com.example.ISOFTBlockchain.account.Account;
import com.example.ISOFTBlockchain.block.Block;
import com.example.ISOFTBlockchain.transaction.Transaction;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/blockchain")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BlockchainController {

    private final BlockchainServices blockchainServices;

    public BlockchainController(BlockchainServices blockchainServices) {
        this.blockchainServices = blockchainServices;
    }

    @GetMapping("")
    public List<Block> getBlockchain() {
        return blockchainServices.getInMemoryBlockchain();
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
    public List<Block> getAccountHistory(@PathVariable("accountNumber") String accountNumber) {
        return blockchainServices.getAccountHistory(accountNumber);
    }

    @GetMapping("/accounts/{accountNumber}/history/last-transaction")
    public Block getLastTransaction(@PathVariable("accountNumber") String accountNumber) {
        return blockchainServices.getLastTransaction(accountNumber);
    }

    @GetMapping("/accounts/{accountNumber}/history/from/{start}/to/{end}")
    public List<Block> getTransactionsWithinPeriod(@PathVariable("accountNumber") String accountNumber,
                                                   @PathVariable("start") Long start,
                                                   @PathVariable("end") Long end) {
        return blockchainServices.getTransactionsInPeriod(accountNumber, start, end);
    }

    @GetMapping("/accounts/{accountNumber}/history/in/{time}")
    public Block getTransactionsInTime(@PathVariable("accountNumber") String accountNumber,
                                            @PathVariable("time") Long time) {
        return blockchainServices.getTransactionInTime(accountNumber, time);
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
