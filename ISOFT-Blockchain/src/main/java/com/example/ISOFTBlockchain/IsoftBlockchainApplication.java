package com.example.ISOFTBlockchain;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IsoftBlockchainApplication {

	public static void main(String[] args) {
		SpringApplication.run(IsoftBlockchainApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner (){
		return runner ->{
//			Transaction transaction = new Transaction();
//			Account account = new Account("Abdullah");
//			transaction.setTransactionType(Constants.ACCOUNT_CREATED);
//			transaction.setSender(account);
//
//			Block block = new Block(transaction, Constants.GENESIS_PREV_HASH);
//			BlockchainServices blockchainServices = new BlockchainServices();
//			List<Block> blockchain = new ArrayList<>();
//
//			System.out.println("Mining ...");
//			blockchainServices.mine(block,blockchain);
//			System.out.println("Mining Done\n");
//
//			System.out.println(block);

			//0000b72606cb6d0715ad9b39ede61d91fd0df224
		};
	}
}
