package com.authcoinandroid.service.qtum;

public class ContractRequest {
    private String[] hashes;

    public ContractRequest(String[] hashes) {
        this.hashes = hashes;
    }

    public String[] getHashes() {
        return hashes;
    }
}