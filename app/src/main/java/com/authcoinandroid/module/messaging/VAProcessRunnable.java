package com.authcoinandroid.module.messaging;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.module.EcKeyFormalValidationModule;
import com.authcoinandroid.module.v2.ChallengeTransporter;
import com.authcoinandroid.module.v2.Triplet;
import com.authcoinandroid.module.v2.ValidationAndAuthenticationProcessingModule;
import com.authcoinandroid.util.Util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Starts a V&A processing module in another thread.
 */
public class VAProcessRunnable implements Runnable {

    // NB! should not be public. DON'T USE IT IN PRODUCTION!
    public static Queue<AuthcoinMessage> queue = new LinkedList<>();
    // NB! should not be public. DON'T USE IT IN PRODUCTION!
    public static Object lock = new Object();
    private final EntityIdentityRecord verifier;
    private final EntityIdentityRecord target;

    private MessageHandler messageHandler;

    public VAProcessRunnable(
            Handler mainHandler,
            EntityIdentityRecord verifier,
            EntityIdentityRecord target
    ) {
        this.messageHandler = new MessageHandler(mainHandler);
        this.verifier = verifier;
        this.target = target;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        Log.i("VAProcessRunnable", "start V&A process: " + Thread.currentThread().getName());
        ValidationAndAuthenticationProcessingModule t = new ValidationAndAuthenticationProcessingModule(
                messageHandler,
                new EcKeyFormalValidationModule(),
                new ChallengeTransporter() {
                    @Override
                    public ChallengeRecord send(ChallengeRecord r) {
                        //TODO remove
                        return new ChallengeRecord(Util.generateId(), r.getVaeId(), r.getType(), new byte[32], r.getTarget(), r.getVerifier());
                    }

                    @Override
                    public ChallengeResponseRecord send(ChallengeResponseRecord rr) {

                        return new ChallengeResponseRecord(
                                Util.generateId(),
                                rr.getVaeId(),
                                10,
                                new byte[32],
                                new byte[32],
                                new byte[128],
                                // TODO invalid challenge.
                                rr.getChallenge()
                        );
                    }

                    @Override
                    public SignatureRecord send(SignatureRecord sr) {
                        return new SignatureRecord(
                                Util.generateId(),
                                sr.getVaeId(),
                                sr.getBlockNumber(),
                                sr.getExpirationBlock(),
                                false,
                                true,
                                new byte[32],
                                new byte[32],
                                // TODO invalid challenge
                                sr.getChallengeResponse()
                        );
                    }
                }
        );
        Triplet<Pair<ChallengeRecord, ChallengeRecord>, Pair<ChallengeResponseRecord, ChallengeResponseRecord>, Pair<SignatureRecord, SignatureRecord>> resp = t.process(target, verifier);
        messageHandler.send(new UserAuthenticatedMessage(resp.getFirst(), resp.getSecond()), 10);
        Log.i("VAProcessRunnable", "end V&A process. result: " + resp + " " + Thread.currentThread().getName());
    }

}
