package com.example.ISOFTBlockchain;

import com.example.ISOFTBlockchain.blockchain.BlockchainServices;
import com.example.ISOFTBlockchain.networking.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IsoftBlockchainApplication {

	public static void main(String[] args) {
		SpringApplication.run(IsoftBlockchainApplication.class, args);
		BlockchainServices.init();

	}

}
