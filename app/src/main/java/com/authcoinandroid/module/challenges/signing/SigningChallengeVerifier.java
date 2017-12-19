package com.authcoinandroid.module.challenges.signing;

import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.ChallengeVerifier;

import java.security.GeneralSecurityException;
import java.security.Signature;

public class SigningChallengeVerifier implements ChallengeVerifier {

    public boolean verify(EntityIdentityRecord eir, byte[] challenge, byte[] fulFilledChallenge) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initVerify(eir.getPublicKey());
            signature.update(challenge);
            return signature.verify(fulFilledChallenge);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Verifying failed", e);
        }
    }

}
