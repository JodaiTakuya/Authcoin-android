package com.authcoinandroid.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.KeyGenerationAndEstablishBindingModule;
import com.authcoinandroid.module.messaging.ChallengeTypeMessageResponse;
import com.authcoinandroid.module.messaging.EvaluateChallengeMessage;
import com.authcoinandroid.module.messaging.EvaluateChallengeResponseMessage;
import com.authcoinandroid.module.messaging.SignatureMessage;
import com.authcoinandroid.module.messaging.SignatureResponseMessage;
import com.authcoinandroid.module.messaging.UserAuthenticatedMessage;
import com.authcoinandroid.module.messaging.VAProcessRunnable;
import com.authcoinandroid.service.keypair.AndroidKeyPairService;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.ui.fragment.authentication.AuthenticationSuccessfulFragment;
import com.authcoinandroid.ui.fragment.authentication.ChallengeTypeSelectorFragment;
import com.authcoinandroid.ui.fragment.authentication.EirSelectorFragment;
import com.authcoinandroid.ui.fragment.authentication.EvaluateChallengeFragment;
import com.authcoinandroid.ui.fragment.authentication.SignatureFragment;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.List;

public class AuthenticationActivity extends AppCompatActivity {

    private Handler mainThreadHandler;
    private Handler vaThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        EirSelectorFragment eirSelectionFragment = new EirSelectorFragment();
        eirSelectionFragment.setNextButtonListener(v -> {
            EntityIdentityRecord selectedEir = eirSelectionFragment.getSelectedEir();
            //TODO fix dummy target EIR

            KeyGenerationAndEstablishBindingModule eirGen = new KeyGenerationAndEstablishBindingModule(((AuthCoinApplication) getApplication()).getEirRepository(), new AndroidKeyPairService());
            try {
                Pair<KeyPair, EntityIdentityRecord> eir = eirGen.generateAndEstablishBinding(new String[]{"my-target"}, "target");
                vaThreadHandler.post(new VAProcessRunnable(mainThreadHandler, selectedEir, eir.second));
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                        evaluateChallengeFragment.setChallengeRecord(((EvaluateChallengeMessage)msg.obj).getChallenge());
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
                        signatureFragment.setChallengeResponse(((SignatureMessage)msg.obj).getChallengeResponse());
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
