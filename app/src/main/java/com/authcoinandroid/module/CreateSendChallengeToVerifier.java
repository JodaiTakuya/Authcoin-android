package com.authcoinandroid.module;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.module.challenges.Challenges;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.util.Util;

import org.spongycastle.util.encoders.Hex;

/**
 * " CreateSendChallengeToVerifier" module
 *
 * Differences:
 * 1.
 */
public class CreateSendChallengeToVerifier {

    private final CreateChallengeForVerifier challengeCreator;
    private ChallengeService challengeService;

    public CreateSendChallengeToVerifier(ChallengeService challengeService) {
        this.challengeCreator = new CreateChallengeForVerifier(challengeService);
        this.challengeService = challengeService;
    }

    public ChallengeRecord createAndSend(ChallengeRecord crForTarget, String challengeType) {
        ChallengeRecord cr = challengeCreator.createChallenge(crForTarget, challengeType);
        challengeService.registerChallenge(cr);
        return cr;
    }

    /**
     * "CreateChallengeForVerifier" module
     */
    public class CreateChallengeForVerifier {

        private ChallengeService challengeService;

        public CreateChallengeForVerifier(ChallengeService challengeService) {
            this.challengeService = challengeService;
        }

        public ChallengeRecord createChallenge(ChallengeRecord crForTarget, String challengeType) {
            boolean processed = challengeService.isProcessed(crForTarget.getVaeId());
            if (processed) {
                throw new IllegalStateException("Vae Id " + Hex.toHexString(crForTarget.getId()) + " is already processed");
            }
            Challenge c = Challenges.get(challengeType);
            byte[] crId = Util.generateId();
            return new ChallengeRecord(crId, crForTarget.getVaeId(), c.getType(), c.getContent(), crForTarget.getTarget(), crForTarget.getVerifier());
        }
    }

}
