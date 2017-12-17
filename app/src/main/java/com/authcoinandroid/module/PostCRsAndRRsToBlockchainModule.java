package com.authcoinandroid.module;


import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.service.challenge.ChallengeService;

/**
 * "PostCRsAndRRsToBlockchain" module
 * <p>
 * Differences:
 * 1. CR and RR is posted to BC after it is created
 * 2. 48h timelimit isn't implemented (yet)
 * 3. no cleanup
 */
public class PostCRsAndRRsToBlockchainModule {

    private ChallengeService challengeService;

    public PostCRsAndRRsToBlockchainModule(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    public void postChallengeRecord(ChallengeRecord rc) {
        challengeService.registerChallenge(rc);
    }

    public void postChallengeResponseRecord(ChallengeResponseRecord rr) {
        challengeService.registerChallengeResponse(rr.getChallenge().getId(), rr);
    }

}
