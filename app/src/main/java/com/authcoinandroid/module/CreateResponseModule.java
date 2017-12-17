package com.authcoinandroid.module;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.module.challenges.ChallengeExecutor;
import com.authcoinandroid.module.challenges.Challenges;
import com.authcoinandroid.module.challenges.signing.SigningChallenge;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.util.Util;

/**
 * "CreateResponse" module
 *
 * Differences:
 * 1. RR_ID counter is replaced by java.util.UUID.randomUUID()o
 *
 */
public class CreateResponseModule {

    private ChallengeService challengeService;

    public CreateResponseModule(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    public ChallengeResponseRecord declineChallenge(ChallengeRecord cr) {
        if(cr.getResponseRecord() != null) {
            throw new IllegalStateException("Challenge response already present");
        }
        // TODO decline challenge
        return null;
    }

    public ChallengeResponseRecord acceptChallenge(ChallengeRecord cr) {
        if(cr.getResponseRecord() != null) {
            throw new IllegalStateException("Challenge response already present");
        }
        ChallengeExecutor executor = Challenges.getExecutor(cr.getType());
        byte[] result = executor.execute(cr.getChallenge(), cr.getTarget());
        return new ChallengeResponseRecord(Util.generateId(), result, cr);
    }
}
