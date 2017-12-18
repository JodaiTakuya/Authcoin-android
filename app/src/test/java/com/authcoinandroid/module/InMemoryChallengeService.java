package com.authcoinandroid.module;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.service.qtum.model.SendRawTransactionResponse;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.bitcoinj.crypto.DeterministicKey;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryChallengeService implements ChallengeService {

    private Map<byte[], List<ChallengeRecord>> vaeIdToChallenges = new HashMap<>();

    @Override
    public Single<ChallengeRecord> registerChallenge(ChallengeRecord challenge) {
        List<ChallengeRecord> challenges = vaeIdToChallenges.get(challenge.getVaeId());
        if (challenges == null) {
            challenges = new LinkedList<>();
        }
        challenges.add(challenge);
        vaeIdToChallenges.put(challenge.getVaeId(), challenges);
        return null;
    }

    @Override
    public Observable<SendRawTransactionResponse> saveChallengeToBc(DeterministicKey key, ChallengeRecord challenge) {
        return null;
    }

    @Override
    public boolean isProcessed(byte[] vaeId) {
        return vaeIdToChallenges.get(vaeId).size() > 1;
    }

    @Override
    public Observable<List<ChallengeRecord>> getChallengeRecordsForEir(EntityIdentityRecord eir) {
        return null;
    }

    @Override
    public Single<ChallengeRecord> registerChallengeResponse(byte[] challengeId, ChallengeResponseRecord response) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public Observable<SendRawTransactionResponse> saveChallengeResponseToBc(DeterministicKey key, ChallengeResponseRecord response) {
        return null;
    }

    @Override
    public Single<ChallengeRecord> registerSignatureRecord(byte[] challengeId, SignatureRecord signature) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public Observable<SendRawTransactionResponse> saveSignatureRecordToBc(DeterministicKey key, SignatureRecord signatureRecord) {
        return null;
    }
}
