package com.example.ISOFTBlockchain;

import com.example.ISOFTBlockchain.blockchain.BlockchainServices;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IsoftBlockchainApplication {

	public static void main(String[] args) {
		SpringApplication.run(IsoftBlockchainApplication.class, args);
		BlockchainServices.init();
	}


}
