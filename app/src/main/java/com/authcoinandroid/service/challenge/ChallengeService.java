package com.authcoinandroid.service.challenge;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import io.reactivex.Observable;

import java.util.List;

public interface ChallengeService {

    void registerChallenge(ChallengeRecord challenge);

    boolean isProcessed(byte[] vaeId);

    Observable<List<ChallengeRecord>> getChallengeRecordsForEir(EntityIdentityRecord eir);
}
