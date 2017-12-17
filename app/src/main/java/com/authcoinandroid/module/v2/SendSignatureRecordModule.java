package com.authcoinandroid.module.v2;

import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;

/**
 * Differences:
 * 1. this module does not exist on CPN module
 */
public class SendSignatureRecordModule {

    private ChallengeTransporter transporter;

    public SendSignatureRecordModule(ChallengeTransporter transporter) {
        this.transporter = transporter;
    }

    public SignatureRecord send(SignatureRecord sr) {
        return transporter.send(sr);
    }
}
