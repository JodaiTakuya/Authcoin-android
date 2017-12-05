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
}
