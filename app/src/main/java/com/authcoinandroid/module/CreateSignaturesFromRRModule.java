package com.authcoinandroid.module;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.util.Util;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.spongycastle.util.encoders.Hex;

/**
 * "CreateSignaturesFromRR" module:
 * <p>
 * Differences:
 * 1. Signature lifespan is in blocks (one block ~ 2,5 minutes)
 * 2. 'satisfied' is an input parameter
 */
public class CreateSignaturesFromRRModule {

    private ChallengeService challengeService;
    private static final int LIFESPAN = 210240; // ~ one year

    public CreateSignaturesFromRRModule(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    public Single<ChallengeRecord> createSignatureRecord(ChallengeResponseRecord rr, boolean satisfied) {
        byte[] challengeId = rr.getChallenge().getId();
        Maybe<ChallengeRecord> m = challengeService.get(challengeId);
        ChallengeRecord challenge = m.blockingGet();
        ChallengeResponseRecord resp = challenge.getResponse();
        if (resp == null) {
            throw new IllegalStateException("Challenge with id " + Hex.toHexString(challengeId) + " doesn't have response record");
        }
        if (resp.getSignatureRecord() != null) {
            throw new IllegalStateException("Challenge with id " + Hex.toHexString(challengeId) + " already has signature record");
        }

        // TODO block number
        Integer startBlock = 10;
        SignatureRecord sr = new SignatureRecord(Util.generateId(), startBlock, startBlock + LIFESPAN, satisfied, rr);

        return challengeService.registerSignatureRecord(challengeId, sr);
    }
}
