package com.example.ISOFTBlockchain.block;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BlockService {
    private final BlockRepository blockRepository;

    public BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    public void save(Block block) {
        blockRepository.save(block);
    }

    public List<Block> getBlockchain() {
        return blockRepository.findAll();
    }

    public Block getLastBlock() {
        List<Block> blocks = getBlockchain();
        if (blocks.isEmpty()) return null;
        return blocks.get(blocks.size() - 1);
    }

}
