package com.authcoinandroid.model;

import io.requery.Entity;
import io.requery.Key;
import io.requery.OneToOne;

@Entity
public class BaseValidationAndAuthenticationEntry {

    @Key
    byte[] id;

    Long blockNumber;

    String description;

    //@OneToOne
   // BaseChallengeRequest verifierChallenge;

    //@OneToOne
    //BaseChallengeRequest targetChallenge;

}
