package com.authcoinandroid.util.crypto;

import java.security.*;

public class CryptoUtil {
    public static KeyPair createEcKeyPair() throws NoSuchAlgorithmException {
        return KeyPairGenerator.getInstance("EC").generateKeyPair();
    }

    public static byte[] sign(PrivateKey privateKey, byte[] data) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        Signature sig = resolveSignatureAlgorithmByKeyAlgorithm(privateKey.getAlgorithm());
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    public static boolean verify(PublicKey publicKey, byte[] signature, byte[] data) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        final Signature sig = resolveSignatureAlgorithmByKeyAlgorithm(publicKey.getAlgorithm());
        sig.initVerify(publicKey);
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
}