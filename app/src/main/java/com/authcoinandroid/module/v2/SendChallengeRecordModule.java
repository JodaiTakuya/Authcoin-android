package com.authcoinandroid.module.v2;

import com.authcoinandroid.model.ChallengeResponseRecord;

/**
 * Differences:
 * 1. this module does not exist on CPN module
 */
public class SendChallengeRecordModule {

    private ChallengeTransporter transporter;

    public SendChallengeRecordModule(ChallengeTransporter transporter) {
        this.transporter = transporter;
    }

    public ChallengeResponseRecord send(ChallengeResponseRecord verifierResponse) {
        return transporter.send(verifierResponse);
    }
}
