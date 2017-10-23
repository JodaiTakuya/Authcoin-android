package com.authcoinandroid.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.model.Identity;
import com.authcoinandroid.task.IdentityCreationTask;
import com.authcoinandroid.task.response.IdentityCreationResponse;
import com.authcoinandroid.task.result.AsyncTaskResult;
import com.authcoinandroid.ui.activity.MainActivity;

public class WelcomeFragment extends Fragment {
    private final static String LOG_TAG = "WelcomeFragment";

    @BindView(R.id.et_create_identity_password)
    EditText createIdentityPassword;

    @BindView(R.id.et_create_identity_password_confirm)
    EditText createIdentityPasswordConfirm;

    @BindView(R.id.pb_identity_creation)
    ProgressBar progressBarIdentityCreation;

    @BindView(R.id.btn_create_identity)
    Button buttonCreateIdentity;

    public WelcomeFragment() {
    }

    @OnClick({R.id.btn_create_identity})
    void onCreateIdentityClick(View view) {
        String password = createIdentityPassword.getText().toString();
        final Context context = this.getContext();
        disableElements();
        new IdentityCreationTask(context, password, new IdentityCreationResponse() {
            @Override
            public void processFinish(AsyncTaskResult<Identity> result) {
                if (result.getError() != null) {
                    enableElements();
                    // TODO display errors
                    Log.d(LOG_TAG, "Failed to create identity");
                } else {
                    Log.d(LOG_TAG, "Created identity");
                    Log.d(LOG_TAG, "Wallet mnemonic code: " + result.getResult().getWallet().getKeyChainSeed().getMnemonicCode());
                    Toast.makeText(context, "Identity created", Toast.LENGTH_LONG).show();
                    clearInputFields();
                    getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
                    ((MainActivity) getActivity()).applyFragment(IdentityFragment.class, false);
                }
            }
        }).execute();
    }

    private void clearInputFields() {
        createIdentityPassword.setText("");
        createIdentityPasswordConfirm.setText("");
    }

    private void disableElements() {
        progressBarIdentityCreation.setVisibility(View.VISIBLE);
        buttonCreateIdentity.setEnabled(false);
        buttonCreateIdentity.setVisibility(View.INVISIBLE);
        createIdentityPassword.setEnabled(false);
        createIdentityPasswordConfirm.setEnabled(false);
    }

    private void enableElements() {
        progressBarIdentityCreation.setVisibility(View.INVISIBLE);
        buttonCreateIdentity.setVisibility(View.VISIBLE);
        buttonCreateIdentity.setEnabled(true);
        createIdentityPassword.setEnabled(true);
        createIdentityPasswordConfirm.setEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
