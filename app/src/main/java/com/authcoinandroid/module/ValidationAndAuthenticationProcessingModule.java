package com.authcoinandroid.module;


import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.util.Util;

import java.util.Arrays;

import static com.authcoinandroid.util.Util.notNull;

/**
 * "V&A-Processing" module.
 * <p>
 * Differences:
 * 1. VAE_ID counter is replaced by java.util.UUID.randomUUID()
 * 2. See differences in submodules.
 * <p>
 * // TODO currently only challenge creation is implemented
 */
public class ValidationAndAuthenticationProcessingModule {

    private FormalValidationModule formalValidationModule;
    private CreateSendChallengeToTarget targetChallengeCreator;
    private CreateSendChallengeToVerifier verifierChallengeCreator;

    public ValidationAndAuthenticationProcessingModule(
            FormalValidationModule formalValidationModule,
            ChallengeService challengeService) {
        notNull(formalValidationModule, "Formal validation module");
        this.formalValidationModule = formalValidationModule;
        this.targetChallengeCreator = new CreateSendChallengeToTarget(challengeService);
        this.verifierChallengeCreator = new CreateSendChallengeToVerifier(challengeService);
    }

    public ChallengeRecord createChallengeForTarget(
            EntityIdentityRecord target,
            EntityIdentityRecord verifier,
            String challengeType) {
        // 1. prepare verifier and target for processing. generate VAE id.
        notNull(target, "Target EIR");
        notNull(verifier, "Verifier EIR");
        notNull(challengeType, "Challenge Type");
        if (Arrays.equals(target.getId(), verifier.getId())) {
            throw new IllegalArgumentException("target EIR == verifier EIR");
        }
        byte[] vaeId = Util.generateId();

        // 2. formal validation.
        boolean success = formalValidationModule.verify(target, verifier);
        if (!success) {
            throw new IllegalStateException("formal validation failed");
        }

        return targetChallengeCreator.createAndSend(vaeId, verifier, target, challengeType);
    }

    public ChallengeRecord createChallengeForVerifier(ChallengeRecord crForTarget, String challengeType) {
        notNull(crForTarget, "Target Challenge Record");
        notNull(challengeType, "Challenge Type");
        // TODO do we need to verify  EIR values?
        // TODO formal validation
        return verifierChallengeCreator.createAndSend(crForTarget, challengeType);
    }

}
