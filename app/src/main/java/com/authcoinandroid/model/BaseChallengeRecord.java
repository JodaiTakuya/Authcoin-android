package com.authcoinandroid.model;

import io.requery.Entity;
import io.requery.Key;
import io.requery.ManyToOne;

@Entity
public class BaseChallengeRecord {

    @Key
    byte[] id;

    byte[] vaeId;

    long timestamp;

    String type;

    byte[] challenge;

    @ManyToOne
    BaseEntityIdentityRecord verifier;

    @ManyToOne
    BaseEntityIdentityRecord target;

    private byte[] hash;
    private byte[] signature;

    public BaseChallengeRecord(byte[] id, byte[] vaeId, String type, byte[] challenge, BaseEntityIdentityRecord verifier, BaseEntityIdentityRecord target) {
        this.id = id;
        this.vaeId = vaeId;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.challenge = challenge;
        this.verifier = verifier;
        this.target = target;
    }

    public BaseChallengeRecord() {
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getSignature() {
        return signature;
    }
}
