package com.authcoinandroid.service.challenge;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.service.qtum.model.SendRawTransactionResponse;

import org.bitcoinj.crypto.DeterministicKey;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface ChallengeService {

    Single<ChallengeRecord> registerChallenge(ChallengeRecord challenge);

    Observable<SendRawTransactionResponse> saveChallengeToBc(DeterministicKey key, ChallengeRecord challenge);

    boolean isProcessed(byte[] vaeId);

    Observable<List<ChallengeRecord>> getChallengeRecordsForEir(EntityIdentityRecord eir);

    Single<ChallengeRecord> registerChallengeResponse(byte[] challengeId, ChallengeResponseRecord response);

    // TODO remove this
    Observable<SendRawTransactionResponse> saveChallengeResponseToBc(DeterministicKey key, ChallengeResponseRecord response);

    Single<ChallengeRecord> registerSignatureRecord(byte[] challengeId, SignatureRecord signature);


    Maybe<ChallengeRecord> get(byte[] id);

    // TODO remove this
    Observable<SendRawTransactionResponse> saveSignatureRecordToBc(DeterministicKey key, SignatureRecord signatureRecord);
}
