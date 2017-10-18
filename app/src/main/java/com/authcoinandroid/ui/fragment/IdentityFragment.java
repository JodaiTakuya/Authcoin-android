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
import com.authcoinandroid.service.WalletService;
import com.authcoinandroid.task.IdentityCreationTask;
import com.authcoinandroid.task.response.IdentityCreationResponse;
import com.authcoinandroid.task.result.AsyncTaskResult;
import com.authcoinandroid.util.AuthCoinNetParams;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import static org.bitcoinj.wallet.KeyChain.KeyPurpose.RECEIVE_FUNDS;

public class IdentityFragment extends Fragment {
    private final static String LOG_TAG = "IdentityFragment";

    @BindView(R.id.et_create_identity_password)
    EditText createIdentityPassword;

    @BindView(R.id.et_create_identity_password_confirm)
    EditText createIdentityPasswordConfirm;

    @BindView(R.id.tv_wallet_address)
    TextView walletAddress;

    @BindView(R.id.pb_identity_creation)
    ProgressBar progressBarIdentityCreation;

    @BindView(R.id.btn_create_identity)
    Button buttonCreateIdentity;

    public IdentityFragment() {
    }

    @OnClick({R.id.btn_create_identity})
    void onCreateIdentityClick(View view) {
        String password = createIdentityPassword.getText().toString();
        final Context context = this.getContext();
        disableElements();
        new IdentityCreationTask(context, password, new IdentityCreationResponse() {
            @Override
            public void processFinish(AsyncTaskResult<Identity> result) {
                enableElements();
                if (result.getError() != null) {
                    // TODO display errors
                    Log.d(LOG_TAG, "Failed to create identity");
                } else {
                    // TODO move user to identity screen.
                    Log.d(LOG_TAG, "Created identity");
                    Log.d(LOG_TAG, "Wallet mnemonic code: " + result.getResult().getWallet().getKeyChainSeed().getMnemonicCode());
                    Toast.makeText(context, "Identity created", Toast.LENGTH_LONG).show();
                    clearInputFields();
                    displayWalletAddress(result.getResult().getWallet());
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
        createIdentityPassword.setEnabled(false);
        createIdentityPasswordConfirm.setEnabled(false);
    }

    private void enableElements() {
        progressBarIdentityCreation.setVisibility(View.INVISIBLE);
        buttonCreateIdentity.setEnabled(true);
        createIdentityPassword.setEnabled(true);
        createIdentityPasswordConfirm.setEnabled(true);
    }

    @OnClick({R.id.btn_delete_identity})
    void onDeleteWallet(View view) {
        String password = createIdentityPassword.getText().toString();
        Log.d(LOG_TAG, "Password is: " + password);
        WalletService.getInstance().deleteWallet(this.getContext());
        displayWalletAddress();
    }

    private void displayWalletAddress(Wallet wallet) {
        // this whole displaying stuff will move to separate view
        walletAddress.setText("");
        DeterministicKey key = wallet.getActiveKeyChain().getKey(RECEIVE_FUNDS);
        walletAddress.setText(key.toAddress(AuthCoinNetParams.getNetParams()).toString());
    }

    private void displayWalletAddress() {
        walletAddress.setText("");
        try {
            Wallet wallet = WalletService.getInstance().loadWalletFromFile(this.getContext());
            displayWalletAddress(wallet);
        } catch (UnreadableWalletException e) {
            // TODO show exceptions nicely
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.identity_fragment, container, false);
        ButterKnife.bind(this, view);
        displayWalletAddress();
        return view;
    }
}
