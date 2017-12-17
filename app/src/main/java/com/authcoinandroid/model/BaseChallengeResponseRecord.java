package com.authcoinandroid.model;

import io.requery.Column;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Key;
import io.requery.OneToOne;

@Entity
public class BaseChallengeResponseRecord {

    @Key
    byte[] id;

    byte[] vaeId;

    @Column(nullable = true)
    Integer blockNumber;

    long timestamp;

    byte[] response;

    byte[] hash;

    byte[] signature;

    @ForeignKey
    @OneToOne
    ChallengeRecord challenge;

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

    public BaseChallengeResponseRecord(byte[] id, byte[] response, ChallengeRecord challenge) {
        this.id = id;
        this.vaeId = challenge.getVaeId();
        this.timestamp = System.currentTimeMillis();
        this.response = response;
        //TODO calculate hash
        this.hash = new byte[32];
        //TODO calculate signature
        this.signature = new byte[128];
        this.challenge = challenge;
    }

    public BaseChallengeResponseRecord() {
    }

    public SignatureRecord getSignatureRecord() {
        return signatureRecord;
    }

    public void setSignatureRecord(SignatureRecord signatureRecord) {
        this.signatureRecord = signatureRecord;
    }
}
