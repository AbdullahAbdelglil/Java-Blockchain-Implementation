package com.example.ISOFTBlockchain.block;

import com.example.ISOFTBlockchain.transaction.Transaction;

public class BlockError extends Block{
    private String error_message;

    public BlockError(String error_message) {
        this.error_message = error_message;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
}
