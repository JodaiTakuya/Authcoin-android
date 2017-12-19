package com.authcoinandroid.ui.activity;

import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.messaging.*;
import com.authcoinandroid.service.challenge.ChallengeServiceImpl;
import com.authcoinandroid.service.transport.HttpRestAuthcoinTransport;
import com.authcoinandroid.service.transport.ServerInfo;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.ui.fragment.authentication.*;
import com.authcoinandroid.module.messaging.*;
import com.authcoinandroid.service.keypair.AndroidKeyPairService;
import com.authcoinandroid.ui.fragment.authentication.*;

import java.security.PublicKey;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AuthActivity";
    private Handler mainThreadHandler;
    private Handler vaThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String demoServer = "http://authcoin-demo-server.cfapps.io";
        ChallengeServiceImpl challengeService = ((AuthCoinApplication) getApplication()).getChallengeService();
        HttpRestAuthcoinTransport transport =
                new HttpRestAuthcoinTransport(demoServer, challengeService);
        EirSelectorFragment eirSelectionFragment = new EirSelectorFragment();
        eirSelectionFragment.setNextButtonListener(v -> {
            EntityIdentityRecord selectedEir = eirSelectionFragment.getSelectedEir();

            new Thread(() -> {
                ServerInfo serverInfo = transport.start();
                EntityIdentityRecord target = ((AuthCoinApplication) getApplication()).getIdentityService().getEirById(serverInfo.getServerEir()).blockingSingle();
                ((AuthCoinApplication) getApplication()).getEirRepository().save(target).blockingGet();
                vaThreadHandler.post(new VAProcessRunnable(mainThreadHandler, selectedEir, target, transport, challengeService));
            }).start();
        });

        transaction.replace(R.id.container, eirSelectionFragment).commit();

        HandlerThread handlerThread = new HandlerThread("VA");
        handlerThread.start();

        mainThreadHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                Log.e("MT", "Main thread: " + Thread.currentThread().getName());
                switch (msg.what) {
                    case 1:
                        // challenge type
                        ChallengeTypeSelectorFragment selectionFragment = new ChallengeTypeSelectorFragment();
                        selectionFragment.setSelectChallengeButtonListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new ChallengeTypeMessageResponse(selectionFragment.getSelectedChallenge()));
                                        VAProcessRunnable.lock.notify();
                                    }
                                }
                        );
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, selectionFragment).commit();
                        break;

                    case 2:
                        // evaluate target's challenge sent to verifier
                        EvaluateChallengeFragment evaluateChallengeFragment = new EvaluateChallengeFragment();
                        evaluateChallengeFragment.setChallengeRecord(((EvaluateChallengeMessage) msg.obj).getChallenge());
                        evaluateChallengeFragment.setApproveButtonListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new EvaluateChallengeResponseMessage(evaluateChallengeFragment.isApproved()));
                                        VAProcessRunnable.lock.notify();
                                    }
                                }
                        );
                        startFragment(evaluateChallengeFragment);
                        break;
                    case 3:
                        SignatureFragment signatureFragment = new SignatureFragment();
                        signatureFragment.setChallengeResponse(((SignatureMessage) msg.obj).getChallengeResponse());
                        signatureFragment.setApproveSignatureListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new SignatureResponseMessage(signatureFragment.getLifespan(), true));
                                        VAProcessRunnable.lock.notify();
                                    }
                                }
                        );
                        startFragment(signatureFragment);
                        break;
                    case 10:
                        AuthenticationSuccessfulFragment asf = new AuthenticationSuccessfulFragment();
                        asf.setResult(((UserAuthenticatedMessage) msg.obj));
                        startFragment(asf);
                        break;
                }
            }
        };

        vaThreadHandler = new Handler(handlerThread.getLooper());
    }

    private void startFragment(Fragment f) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, f).commit();
    }

}
