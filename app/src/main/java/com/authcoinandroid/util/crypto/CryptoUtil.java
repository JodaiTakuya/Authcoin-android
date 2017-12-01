package com.authcoinandroid.util.crypto;

import java.io.IOException;
import java.security.*;
import java.util.Collections;
import java.util.List;

public class CryptoUtil {

    public static PublicKey getPublicKeyByAlias(String alias) throws GeneralSecurityException, IOException {
        return getEntry(alias).getCertificate().getPublicKey();
    }

    public static byte[] sign(byte[] data, String alias) throws GeneralSecurityException, IOException {
        KeyStore.PrivateKeyEntry entry = getEntry(alias);
        PrivateKey privateKey = entry.getPrivateKey();
        Signature sig = resolveSignatureAlgorithmByKeyAlgorithm(privateKey.getAlgorithm());
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    public static byte[] sign(byte[] data, KeyPair keyPair) throws GeneralSecurityException, IOException {
        PrivateKey privateKey = keyPair.getPrivate();
        Signature sig = resolveSignatureAlgorithmByKeyAlgorithm(privateKey.getAlgorithm());
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    public static boolean verify(byte[] signature, byte[] data, String alias) throws GeneralSecurityException, IOException {
        KeyStore.PrivateKeyEntry entry = getEntry(alias);
        final Signature sig = resolveSignatureAlgorithmByKeyAlgorithm(entry.getPrivateKey().getAlgorithm());
        sig.initVerify(entry.getCertificate());
        sig.update(data);
        return sig.verify(signature);
    }

    public static byte[] hashSha256(String data) throws NoSuchAlgorithmException {
        return hash(MessageDigest.getInstance("SHA-256"), data);
    }

    public static List<String> getAliases() throws GeneralSecurityException, IOException {
        return Collections.list(getKeyStore().aliases());
    }

    private static KeyStore.PrivateKeyEntry getEntry(String alias) throws GeneralSecurityException, IOException {
        KeyStore ks = getKeyStore();
        KeyStore.Entry entry = ks.getEntry(alias, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            throw new InvalidKeyException();
        }
        return (KeyStore.PrivateKeyEntry) entry;
    }

    private static KeyStore getKeyStore() throws GeneralSecurityException, IOException {
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
}