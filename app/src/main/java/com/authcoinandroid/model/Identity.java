package com.authcoinandroid.model;

import org.bitcoinj.wallet.Wallet;

public class Identity {
    private Wallet wallet;

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}