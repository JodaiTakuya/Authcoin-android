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

    public static byte[] sign(byte[] data, String alias) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException, CertificateException, KeyStoreException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(alias, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            return null;
        }
        PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        Signature sig = resolveSignatureAlgorithmByKeyAlgorithm(privateKey.getAlgorithm());
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    public static boolean verify(byte[] signature, byte[] data, String alias) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableEntryException {
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(alias, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            return false;
        }
        PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        final Signature sig = resolveSignatureAlgorithmByKeyAlgorithm(privateKey.getAlgorithm());
        sig.initVerify(((KeyStore.PrivateKeyEntry) entry).getCertificate());
        sig.update(data);
        return sig.verify(signature);
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