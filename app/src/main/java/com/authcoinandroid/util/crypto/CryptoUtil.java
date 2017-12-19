package com.authcoinandroid.util.crypto;

import android.security.keystore.KeyProperties;

import org.spongycastle.jce.spec.ECPublicKeySpec;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.List;

public class CryptoUtil {

    public static PublicKey getPublicKeyByAlias(String alias) throws GeneralSecurityException, IOException {
        return getEntry(alias).getCertificate().getPublicKey();
    }

    public static KeyPair getKeyPair(String alias) {
        try {
            KeyStore keyStore = getKeyStore();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);
            PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();
            return new KeyPair(publicKey, privateKey);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static KeyStore.PrivateKeyEntry getEntry(String alias) throws GeneralSecurityException, IOException {
        KeyStore ks = getKeyStore();
        KeyStore.Entry entry = ks.getEntry(alias, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            throw new InvalidKeyException();
        }
        return (KeyStore.PrivateKeyEntry) entry;
    }

    public static KeyStore getKeyStore() throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
        return ks;
    }

    private static byte[] hash(MessageDigest messageDigest, String data) {
        messageDigest.update(data.getBytes());
        return messageDigest.digest();
    }

    private static Signature resolveSignatureAlgorithmByKeyAlgorithm(String keyAlgorithm) throws NoSuchAlgorithmException {
        String signatureAlgorithm;
        switch (keyAlgorithm) {
            case "EC":
                signatureAlgorithm = "SHA256WithECDSA";
                break;
            default:
                throw new NoSuchAlgorithmException();
        }
        return Signature.getInstance(signatureAlgorithm);
    }

    public static PublicKey toPublicKey(byte[] content) {
        try {
            return KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC).generatePublic(new X509EncodedKeySpec(content));
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}