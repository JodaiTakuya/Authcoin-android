package com.authcoinandroid.module;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.authcoinandroid.service.keypair.KeyPairException;
import com.authcoinandroid.service.keypair.KeyPairService;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.junit.Ignore;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.spec.ECParameterSpec;
import java.util.HashMap;
import java.util.Map;

@Ignore
public class JdkKeyPairService implements KeyPairService {

    private final Map<String, KeyPair> keyPairs = new HashMap<>();

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public KeyPair create(String alias) {
        try {
            ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime192v1");
            KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");
            g.initialize(ecSpec);
            KeyPair keyPair = g.generateKeyPair();
            keyPairs.put(alias, keyPair);
            return keyPair;
        } catch (GeneralSecurityException e) {
            throw new KeyPairException("KeyPair generation failed", e);
        }
    }

    @Override
    public KeyPair get(String alias) {
        return keyPairs.get(alias);
    }
}
