package com.authcoinandroid.service;

import android.content.Context;
import com.authcoinandroid.model.Identity;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.IOException;

public class IdentityService {

    private static IdentityService identityService;

    public static IdentityService getInstance() {
        if (identityService == null) {
            identityService = new IdentityService();
        }
        return identityService;
    }

    private IdentityService() {
    }

    public Identity createIdentity(Context context, String password) throws IOException, UnreadableWalletException {
        Wallet wallet = WalletService.getInstance().createWallet(context, password);
        Identity identity = new Identity();
        identity.setWallet(wallet);
        return identity;
    }
}