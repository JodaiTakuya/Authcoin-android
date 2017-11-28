package com.authcoinandroid.util.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class CryptoUtil {
    public static KeyPair createEcKeyPair(String alias) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        return generateKeyPair(alias, KeyProperties.KEY_ALGORITHM_EC);
    }

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

    public static boolean verify(byte[] signature, byte[] data, String alias) throws GeneralSecurityException, IOException {
        KeyStore.PrivateKeyEntry entry = getEntry(alias);
        final Signature sig = resolveSignatureAlgorithmByKeyAlgorithm(entry.getPrivateKey().getAlgorithm());
        sig.initVerify(entry.getCertificate());
        sig.update(data);
        return sig.verify(signature);
    }

    private static KeyStore.PrivateKeyEntry getEntry(String alias) throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(alias, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            throw new InvalidKeyException();
        }
        return (KeyStore.PrivateKeyEntry) entry;
    }

    public static byte[] hashSha256(String data) throws NoSuchAlgorithmException {
        return hash(MessageDigest.getInstance("SHA-256"), data);
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

    private static KeyPair generateKeyPair(String alias, String keyAlgorithm) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(keyAlgorithm, "AndroidKeyStore");
        kpg.initialize(
                new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .build());
        return kpg.generateKeyPair();
    }
}