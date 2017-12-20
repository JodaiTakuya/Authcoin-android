package com.authcoinandroid.module;

import android.util.Pair;

import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.module.messaging.MessageHandler;
import com.authcoinandroid.service.transport.AuthcoinTransport;

public class CreateSignaturesModule {

    private CreateSignaturesFromRRModule createSignaturesModule;

    public CreateSignaturesModule(MessageHandler messageHandler, AuthcoinTransport transporter) {
        this.createSignaturesModule = new CreateSignaturesFromRRModule(messageHandler, transporter);
    }

    public Pair<SignatureRecord, SignatureRecord> process(ChallengeResponseRecord response) {
        return createSignaturesModule.process(response);
    }
}
