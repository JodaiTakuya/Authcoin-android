package com.authcoinandroid.service.identity;

import com.authcoinandroid.model.EntityIdentityRecord;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import static com.authcoinandroid.service.identity.EcEirBuilder.newEcEirBuilder;
import static com.authcoinandroid.util.crypto.CryptoUtil.createEcKeyPair;

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

    public void registerEirWithEcKey(String[] identifiers) throws UnreadableWalletException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        KeyPair keyPair = createEcKeyPair();
        EntityIdentityRecord eir = newEcEirBuilder()
                .addIdentifiers(identifiers)
                .addContent(keyPair.getPublic())
                .setContentType()
                .calculateHash()
                .signHash(keyPair.getPrivate())
                .getEir();
    }
}