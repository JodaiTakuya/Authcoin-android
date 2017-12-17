package com.authcoinandroid.module.v2;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;

/**
 * Interface for different challenge related transport mechanisms.
 */
public interface ChallengeTransporter {

    /**
     * Sends CR to target and expects it to return verifier's CR.
     *
     * @param record target CR.
     * @return verifier's CR
     */
    ChallengeRecord send(ChallengeRecord record);

    /**
     * Sends RR to target and expects it to return target's RR.
     *
     * @param verifierResponse verifier CR.
     * @return target's CR
     */
    ChallengeResponseRecord send(ChallengeResponseRecord verifierResponse);

    /**
     * Sends SR to target and expects it to return target's SR.
     *
     * @param sr verifier SR.
     * @return target's SR
     */
    SignatureRecord send(SignatureRecord sr);
}
