package com.authcoinandroid.module.challenges.signing;

import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.ChallengeExecutor;


public class SigningChallengeExecutor implements ChallengeExecutor {

    @Override
    public byte[] execute(byte[] challenge, EntityIdentityRecord eir) {
        //try {
        //Signature signature = Signature.getInstance("SHA256withECDSA");
        //signature.initSign(eir.getKeyPair().getPrivate());
        //signature.update(challenge);
        //TODO
        //return signature.sign();
        return new byte[53];
        //} catch (GeneralSecurityException e) {
        //    throw new IllegalStateException("Signing failed", e);
        //}
    }

}
