package com.authcoinandroid.module;

import android.util.Pair;

import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.module.messaging.MessageHandler;
import com.authcoinandroid.module.messaging.SignatureMessage;
import com.authcoinandroid.module.messaging.SignatureResponseMessage;
import com.authcoinandroid.service.transport.AuthcoinTransport;
import com.authcoinandroid.util.Util;

// Differences:
// 1. new extra module to send RR to target (SendSignatureRecordModule)
// 2. new extra module to post RR to blockchain (not implemented)
public class CreateSignaturesFromRRModule {

    private final MessageHandler messageHandler;
    private final SendSignatureRecordModule sendSignatureRecordModule;

    public CreateSignaturesFromRRModule(MessageHandler messageHandler, AuthcoinTransport transporter) {
        this.messageHandler = messageHandler;
        this.sendSignatureRecordModule = new SendSignatureRecordModule(transporter);
    }

    /**
     * @param rr - target's response record.
     * @return first parameter is target's RR; second parameter is verifiers RR.
     */
    Pair<SignatureRecord, SignatureRecord> process(ChallengeResponseRecord rr) {
        // 1. is CR processed?
        // TODO implement

        // 2. lifespan and is satisfied
        SignatureMessage req = new SignatureMessage(rr);
        SignatureResponseMessage resp =
                (SignatureResponseMessage) messageHandler.sendAndWaitResponse(req, 3);

        // 3. create RR
        int startBlock = 10; // TODO
        SignatureRecord verifierSignature = new SignatureRecord(Util.generateId(), startBlock, startBlock + resp.getLifespan(), resp.isSatisfied(), rr);

        // 4. send to target
        SignatureRecord targetSignature = sendSignatureRecordModule.send(verifierSignature);

        // 5. post to BC.
        // TODO implement

        return Pair.create(targetSignature, verifierSignature);
    }

}
