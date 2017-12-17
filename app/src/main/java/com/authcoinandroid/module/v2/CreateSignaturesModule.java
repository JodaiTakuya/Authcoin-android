package com.authcoinandroid.module.v2;

import android.util.Pair;

import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.module.messaging.MessageHandler;

public class CreateSignaturesModule {

    private CreateSignaturesFromRRModule createSignaturesModule;

    public CreateSignaturesModule(MessageHandler messageHandler, ChallengeTransporter transporter) {
        this.createSignaturesModule = new CreateSignaturesFromRRModule(messageHandler, transporter);
    }

    public Pair<SignatureRecord, SignatureRecord> process(ChallengeResponseRecord response) {
        return createSignaturesModule.process(response);
    }
}
