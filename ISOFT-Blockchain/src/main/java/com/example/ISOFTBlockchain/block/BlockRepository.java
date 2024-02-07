package com.example.ISOFTBlockchain.block;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockRepository extends MongoRepository<Block, String> {
}
