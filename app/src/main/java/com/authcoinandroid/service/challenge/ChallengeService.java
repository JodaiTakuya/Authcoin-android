package com.authcoinandroid.service.challenge;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;

import io.reactivex.Single;

public interface ChallengeService {

    Single<ChallengeRecord> registerChallenge(ChallengeRecord challenge);

    boolean isProcessed(byte[] vaeId);

    Single<ChallengeRecord> registerChallengeResponse(byte[] challengeId, ChallengeResponseRecord response);

    Single<ChallengeRecord> registerSignatureRecord(byte[] challengeId, SignatureRecord signature);
}
