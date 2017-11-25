package com.authcoinandroid.ui.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.service.contract.AuthcoinContractService;
import com.authcoinandroid.service.identity.WalletService;
import com.authcoinandroid.service.qtum.UnspentOutput;
import com.authcoinandroid.ui.activity.MainActivity;
import org.bitcoinj.wallet.UnreadableWalletException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.Locale;

public class IdentityFragment extends Fragment {
    private final static String LOG_TAG = "IdentityFragment";

    @BindView(R.id.tv_wallet_address)
    TextView walletAddress;

    @BindView(R.id.tv_unspent_output)
    TextView unspentOutputTextView;

    public IdentityFragment() {
    }

    @OnClick({R.id.btn_delete_wallet})
    void onDeleteWallet(View view) {
        WalletService.getInstance().deleteWallet(this.getContext());
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.INVISIBLE);
        ((MainActivity) getActivity()).applyFragment(WelcomeFragment.class, false);
    }

    @OnClick({R.id.tv_wallet_address})
    void onCopyWalletAddress(View view) {
        ClipboardManager clipboard = (ClipboardManager) this.getContext().getSystemService(Activity.CLIPBOARD_SERVICE);
        String walletAddressLabel = getString(R.string.wallet_address);
        clipboard.setPrimaryClip(ClipData.newPlainText(walletAddressLabel.substring(0, walletAddressLabel.length()-1), walletAddress.getText()));
        Toast.makeText(this.getContext(), getString(R.string.wallet_address_copied), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.identity_fragment, container, false);
        ButterKnife.bind(this, view);
        displayWalletAddress();
        displayUnspentOutputAmount();
        return view;
    }

    private void displayWalletAddress() {
        try {
            String walletAddress = WalletService.getInstance().getWalletAddress(this.getContext());
            Log.d(LOG_TAG, "Wallet address is: " + walletAddress);
            this.walletAddress.setText(walletAddress);
        } catch (UnreadableWalletException e) {
            Log.e(LOG_TAG, "Unable to load wallet address: " + e.getMessage());
            ((MainActivity) getActivity()).displayError(LOG_TAG, e.getMessage());
        }
    }

    private void displayUnspentOutputAmount() {
        try {
            AuthcoinContractService.getInstance().getUnspentOutputs(WalletService.getInstance().getReceiveKey(this.getContext()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<UnspentOutput>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (isAdded()) {
                                unspentOutputTextView.setText(getString(R.string.missing_value));
                            }
                            ((MainActivity) getActivity()).displayError(LOG_TAG, e.getMessage());
                        }

                        @Override
                        public void onNext(List<UnspentOutput> unspentOutputs) {
                            if (isAdded()) {
                                unspentOutputTextView.setText(
                                        String.format(Locale.getDefault(), "%f", unspentOutputs.get(0).getAmount())
                                );
                            }
                        }
                    });
        } catch (UnreadableWalletException e) {
            ((MainActivity) getActivity()).displayError(LOG_TAG, e.getMessage());
        }
    }
}