package com.authcoinandroid.service.challenge;

import com.authcoinandroid.model.ChallengeRecord;

public interface ChallengeService {

    void registerChallenge(ChallengeRecord challenge);

    boolean isProcessed(byte[] vaeId);

}
