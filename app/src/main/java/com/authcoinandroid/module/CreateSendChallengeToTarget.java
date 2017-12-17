package com.authcoinandroid.module;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.module.challenges.Challenges;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.util.Util;

/**
 * “CreateSendChallengeToTarget” module
 * <p>
 * Differences:
 * 1. BC is used to send challenge to counterparty
 * 2. A little bit different implementation
 * 3. Challenge Type is one input parameter
 */
public class CreateSendChallengeToTarget {

    private CreateChallengeForTarget challengeCreator = new CreateChallengeForTarget();
    private SendChallengeToTarget challengeSender;

    public CreateSendChallengeToTarget(ChallengeService challengeService) {
        this.challengeSender = new SendChallengeToTarget(challengeService);
    }

    public ChallengeRecord createAndSend(byte[] vaeId, EntityIdentityRecord verifier, EntityIdentityRecord target, String challengeType) {
        ChallengeRecord cr = challengeCreator.createChallenge(vaeId, verifier, target, challengeType);
        challengeSender.send(cr);
        return cr;
    }

    /**
     * “CreateSendChallengeToTarget” module
     * <p>
     * Differences:
     * 1. one output parameter - challenge request
     */
    public class CreateChallengeForTarget {

        public ChallengeRecord createChallenge(byte[] vaeId, EntityIdentityRecord verifier, EntityIdentityRecord target, String type) {
            Challenge challenge = Challenges.get(type);
            byte[] crId = Util.generateId();
            return new ChallengeRecord(crId, vaeId, challenge.getType(), challenge.getContent(), verifier, target);
        }

    }

    /**
     * “SendChallengeToTarget” module
     * <p>
     * Differences:
     * 1. BC is used to send challenge to counterparty
     * 2. Used only to send challenge.
     */
    public class SendChallengeToTarget {

        private ChallengeService challengeService;

        public SendChallengeToTarget(ChallengeService challengeService) {
            this.challengeService = challengeService;
        }

        public void send(ChallengeRecord record) {
            challengeService.registerChallenge(record).blockingGet();
        }
    }

}
