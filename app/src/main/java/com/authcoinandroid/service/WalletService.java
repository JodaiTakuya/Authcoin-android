package com.authcoinandroid.service;

import android.content.Context;
import com.authcoinandroid.util.AuthCoinNetParams;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;

public class WalletService {

    private static WalletService walletService;
    private File keyStorageDirectory;
    private Wallet wallet;

    public static WalletService getInstance() {
        if (walletService == null) {
            walletService = new WalletService();
        }
        return walletService;
    }

    private WalletService() {
    }

    public void createWallet(final Context context, String password) throws UnreadableWalletException, IOException {
        keyStorageDirectory = resolveKeyStorageDirectory(context);
        String mnemonicCode = generateMnemonicCode();
        DeterministicSeed seed = new DeterministicSeed(mnemonicCode, null, password, DeterministicHierarchy.BIP32_STANDARDISATION_TIME_SECS);
        wallet = Wallet.fromSeed(AuthCoinNetParams.getNetParams(), seed);
        wallet.saveToFile(keyStorageDirectory);
    }

    public Wallet loadWalletFromFile(Context context) throws UnreadableWalletException {
        keyStorageDirectory = resolveKeyStorageDirectory(context);
        return Wallet.loadFromFile(keyStorageDirectory);
    }

    public void deleteWallet(Context context){
        resolveKeyStorageDirectory(context).delete();
    }

    private File resolveKeyStorageDirectory(Context context) {
        return new File(context.getFilesDir().getPath() + "/authcoin");
    }

    private String generateMnemonicCode() {
        return "when arno arrived at the schoolhouse with his father the classes had already started";
    }
}