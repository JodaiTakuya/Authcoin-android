package com.authcoinandroid.model;

import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Key;
import io.requery.OneToOne;
import io.requery.ReferentialAction;

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

    byte[] hash;

    byte[] signature;

    @ForeignKey(delete = ReferentialAction.CASCADE, update = ReferentialAction.CASCADE)
    @OneToOne
    ChallengeResponseRecord challengeResponse;

    public BaseSignatureRecord(byte[] id, byte[] vaeId, int blockNumber, int expirationBlock, boolean revoked, boolean successful, byte[] hash, byte[] signature, ChallengeResponseRecord challengeResponse) {
        this.id = id;
        this.vaeId = vaeId;
        this.blockNumber = blockNumber;
        this.expirationBlock = expirationBlock;
        this.revoked = revoked;
        this.successful = successful;
        this.timestamp = System.currentTimeMillis();
        this.hash = hash;
        this.signature = signature;
        this.challengeResponse = challengeResponse;
    }

    public BaseSignatureRecord() {
    }
}
