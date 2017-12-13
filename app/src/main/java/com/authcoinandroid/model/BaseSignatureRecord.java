package com.authcoinandroid.model;

import io.requery.Entity;
import io.requery.Key;
import io.requery.OneToOne;

@Entity
public class BaseSignatureRecord {

    @Key
    byte[] id;

    byte[] vaeId;

    int blockNumber;

    int expirationBlock;

    boolean revoked;

    boolean successful;

    long timestamp;

    byte[] response;

    byte[] hash;

    byte[] signature;

    @OneToOne(mappedBy = "signatureRecord")
    ChallengeResponseRecord challengeResponse;

    public BaseSignatureRecord(byte[] id, byte[] vaeId, int blockNumber, int expirationBlock, boolean revoked, boolean successful, long timestamp, byte[] response, byte[] hash, byte[] signature, ChallengeResponseRecord challengeResponse) {
        this.id = id;
        this.vaeId = vaeId;
        this.blockNumber = blockNumber;
        this.expirationBlock = expirationBlock;
        this.revoked = revoked;
        this.successful = successful;
        this.timestamp = timestamp;
        this.response = response;
        this.hash = hash;
        this.signature = signature;
        this.challengeResponse = challengeResponse;
    }

    public BaseSignatureRecord() {
    }
}
