package com.authcoinandroid.service.challenge;

import android.support.annotation.NonNull;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;

import org.spongycastle.util.encoders.Hex;

import io.reactivex.Maybe;
import io.reactivex.Single;


public class ChallengeServiceImpl implements ChallengeService {

    private ChallengeRepository challengeRepository;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public Single<ChallengeRecord> registerChallenge(ChallengeRecord challenge) {
        Single<ChallengeRecord> result = challengeRepository.save(challenge);
        // TODO send data to BC
        return result;
    }

    @Override
    public boolean isProcessed(byte[] vaeId) {
        return challengeRepository.findByVaeId(vaeId).toList().size() < 1;
    }

    @Override
    public Single<ChallengeRecord> registerChallengeResponse(byte[] challengeId, ChallengeResponseRecord response) {
        ChallengeRecord challenge = getChallengeRecord(challengeId);
        if (challenge.getResponseRecord() != null) {
            throw new IllegalStateException("Challenge with id " + Hex.toHexString(challengeId) + " already has response record");
        }
        challenge.setResponseRecord(response);
        return challengeRepository.save(challenge);
    }

    @Override
    public Single<ChallengeRecord> registerSignatureRecord(byte[] challengeId, SignatureRecord signature) {
        ChallengeRecord challenge = getChallengeRecord(challengeId);
        if (challenge.getResponseRecord() == null) {
            throw new IllegalStateException("Challenge with id " + Hex.toHexString(challengeId) + " doesn't response record");
        }
        if (challenge.getResponseRecord().getSignatureRecord() != null) {
            throw new IllegalStateException("ChallengeRecord with id " + Hex.toHexString(challengeId) + " already has signature record");
        }
        challenge.getResponseRecord().setSignatureRecord(signature);
        return challengeRepository.save(challenge);
    }

    @NonNull
    private ChallengeRecord getChallengeRecord(byte[] challengeId) {
        Maybe<ChallengeRecord> m = challengeRepository.find(challengeId);
        ChallengeRecord challenge = m.blockingGet();
        if (challenge == null) {
            throw new IllegalStateException("Challenge with id " + Hex.toHexString(challengeId) + " not found");
        }
        return challenge;
    }


}
