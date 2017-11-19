package com.authcoinandroid.service.identity;

import android.content.Context;
import com.authcoinandroid.util.AuthCoinNetParams;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

import static org.bitcoinj.core.Utils.currentTimeSeconds;
import static org.bitcoinj.wallet.DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS;

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

    Wallet createWallet(final Context context, String password) throws UnreadableWalletException, IOException {
        keyStorageDirectory = resolveKeyStorageDirectory(context);
        DeterministicSeed seed = new DeterministicSeed(new SecureRandom(), DEFAULT_SEED_ENTROPY_BITS, password, currentTimeSeconds());
        wallet = Wallet.fromSeed(AuthCoinNetParams.getNetParams(), seed);
        wallet.saveToFile(keyStorageDirectory);
        return wallet;
    }

    private Wallet loadWalletFromFile(Context context) throws UnreadableWalletException {
        keyStorageDirectory = resolveKeyStorageDirectory(context);
        return Wallet.loadFromFile(keyStorageDirectory);
    }

    public String getWalletAddress(Context context) throws UnreadableWalletException {
        return loadWalletFromFile(context).freshReceiveAddress().toBase58();
    }

    public void deleteWallet(Context context) {
        resolveKeyStorageDirectory(context).delete();
    }

    public DeterministicKey getReceiveKey(Context context) throws UnreadableWalletException {
        return loadWalletFromFile(context).freshReceiveKey();
    }

    public boolean isWalletCreated(Context context) {
        try {
            this.loadWalletFromFile(context);
        } catch (UnreadableWalletException e) {
            return false;
        }
        return true;
    }

    private File resolveKeyStorageDirectory(Context context) {
        return new File(context.getFilesDir().getPath() + "/authcoin");
    }
}