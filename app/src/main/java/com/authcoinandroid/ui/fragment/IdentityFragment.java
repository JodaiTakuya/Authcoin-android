package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.service.WalletService;
import com.authcoinandroid.util.AuthCoinNetParams;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.IOException;

import static org.bitcoinj.wallet.KeyChain.KeyPurpose.RECEIVE_FUNDS;

public class IdentityFragment extends Fragment {
    private final static String LOG_TAG = "IdentityFragment";

    @BindView(R.id.et_create_identity_password)
    EditText createIdentityPassword;

    @BindView(R.id.et_create_identity_password_confirm)
    EditText createIdentityPasswordConfirm;

    @BindView(R.id.tv_wallet_address)
    TextView walletAddress;

    public IdentityFragment() {

    }

    @OnClick({R.id.btn_create_identity})
    public void onCreateIdentityClick(View view) {
        String password = createIdentityPassword.getText().toString();
        // TODO check if passwords match and display errors
        // TODO create wallet on separate thread
        try {
            WalletService.getInstance().createWallet(this.getContext(), password);
        } catch (UnreadableWalletException | IOException e) {
            //TODO handle errors
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btn_delete_identity})
    public void onDeleteWallet(View view) {
        String password = createIdentityPassword.getText().toString();
        Log.d(LOG_TAG, "Password is: " + password);
        WalletService.getInstance().deleteWallet(this.getContext());
    }

    private void displayWalletAddress() {
        walletAddress.setText("");
        try {
            Wallet wallet = WalletService.getInstance().loadWalletFromFile(this.getContext());
//            maybe show it later
//            List<String> mnemonicCode = wallet.getActiveKeyChain().getMnemonicCode();
            DeterministicKey key = wallet.getActiveKeyChain().getKey(RECEIVE_FUNDS);
            walletAddress.setText(key.toAddress(AuthCoinNetParams.getNetParams()).toString());
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
