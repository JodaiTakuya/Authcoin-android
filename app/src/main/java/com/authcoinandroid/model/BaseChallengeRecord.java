package com.authcoinandroid.model;

import io.requery.Entity;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.OneToOne;

@Entity
public class BaseChallengeRecord {

    @Key
    byte[] id;

    byte[] vaeId;

    long timestamp;

    String type;

    byte[] challenge;

    @ManyToOne
    EntityIdentityRecord verifier;

    @ManyToOne
    EntityIdentityRecord target;
    
    @OneToOne(mappedBy = "challenge")
    ChallengeResponseRecord response;

    public BaseChallengeRecord(byte[] id, byte[] vaeId, String type, byte[] challenge, EntityIdentityRecord verifier, EntityIdentityRecord target) {
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

    public void setResponseRecord(ChallengeResponseRecord response) {
        this.response = response;
    }

    public ChallengeResponseRecord getResponseRecord() {
        return response;
    }
}
