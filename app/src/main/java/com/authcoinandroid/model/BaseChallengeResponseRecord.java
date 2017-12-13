package com.authcoinandroid.model;

import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Key;
import io.requery.OneToOne;

@Entity
public class BaseChallengeResponseRecord {

    @Key
    byte[] id;

    byte[] vaeId;

    int blockNumber;

    long timestamp;

    byte[] response;

    byte[] hash;

    byte[] signature;

    @OneToOne(mappedBy = "response")
    ChallengeRecord challenge;

    @ForeignKey
    @OneToOne(mappedBy = "challengeResponse")
    SignatureRecord signatureRecord;

    public BaseChallengeResponseRecord(byte[] id, byte[] vaeId, int blockNumber, byte[] response, byte[] hash, byte[] signature, ChallengeRecord challenge) {
        this.id = id;
        this.vaeId = vaeId;
        this.blockNumber = blockNumber;
        this.timestamp = System.currentTimeMillis();
        this.response = response;
        this.hash = hash;
        this.signature = signature;
        this.challenge = challenge;
    }

    public BaseChallengeResponseRecord() {
    }
}
