package com.authcoinandroid.service.challenge;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.model.SignatureRecord;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.util.List;

public interface ChallengeService {

    Single<ChallengeRecord> registerChallenge(ChallengeRecord challenge);

    boolean isProcessed(byte[] vaeId);

    Observable<List<ChallengeRecord>> getChallengeRecordsForEir(EntityIdentityRecord eir);

    Single<ChallengeRecord> registerChallengeResponse(byte[] challengeId, ChallengeResponseRecord response);

    Single<ChallengeRecord> registerSignatureRecord(byte[] challengeId, SignatureRecord signature);
}
