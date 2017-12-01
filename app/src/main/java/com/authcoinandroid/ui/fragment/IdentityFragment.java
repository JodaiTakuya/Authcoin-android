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
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.authcoinandroid.R;
import com.authcoinandroid.exception.GetAliasException;
import com.authcoinandroid.service.contract.AuthcoinContractService;
import com.authcoinandroid.service.identity.IdentityService;
import com.authcoinandroid.service.identity.WalletService;
import com.authcoinandroid.service.qtum.UnspentOutput;
import com.authcoinandroid.ui.activity.MainActivity;
import com.authcoinandroid.ui.adapter.EirAliasAdapter;
import org.bitcoinj.wallet.UnreadableWalletException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


import java.util.List;
import java.util.Locale;

public class IdentityFragment extends Fragment {
    private final static String LOG_TAG = "IdentityFragment";
    private String mWalletAddress;

    @BindView(R.id.iv_wallet_copy)
    ImageView walletAddressCopy;

    @BindView(R.id.tv_unspent_output)
    TextView unspentOutputTextView;

    @BindView(R.id.lv_eirs)
    ListView eirsListView;

    public IdentityFragment() {
    }

    @OnLongClick({R.id.iv_wallet})
    boolean onDeleteWallet(View view) {
        WalletService.getInstance().deleteWallet(this.getContext());
        ((MainActivity) getActivity()).applyFragment(WelcomeFragment.class, false, true);
        return true;
    }

    @OnClick({R.id.iv_wallet_copy})
    void onCopyWalletAddress(View view) {
        ClipboardManager clipboard = (ClipboardManager) this.getContext().getSystemService(Activity.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Wallet address", mWalletAddress));
        Toast.makeText(this.getContext(), getString(R.string.wallet_address_copied), Toast.LENGTH_LONG).show();
    }

    @OnClick({R.id.btn_new_identity})
    void onOpenAddIdentity(View view) {
        ((MainActivity) getActivity()).applyFragment(NewIdentityFragment.class, true, true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.identity_fragment, container, false);
        ButterKnife.bind(this, view);
        attachWalletAddressToCopyImage();
        displayUnspentOutputAmount();
        displayEirs();
        return view;
    }

    private void displayEirs() {
        try {
            List<String> eirAliases = IdentityService.getInstance().getAllAliases();
            EirAliasAdapter adapter = new EirAliasAdapter(getContext(), eirAliases);
            eirsListView.setAdapter(adapter);

            eirsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String alias = eirAliases.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("alias", alias);

                    if (getContext() instanceof MainActivity) {
                        ((MainActivity) getContext()).applyFullFragmentWithBundle(EirFragment.class, bundle);
                    }
                }
            });
        } catch (GetAliasException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }

    private void attachWalletAddressToCopyImage() {
        try {
            String walletAddress = WalletService.getInstance().getWalletAddress(this.getContext());
            Log.d(LOG_TAG, "Wallet address is: " + walletAddress);
            mWalletAddress = walletAddress;
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
                    .subscribe(new DisposableObserver<List<UnspentOutput>>() {
                        @Override
                        public void onComplete() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (isAdded()) {
                                unspentOutputTextView.setText(getString(R.string.missing_value));
                            }
                        }

                        @Override
                        public void onNext(List<UnspentOutput> unspentOutputs) {
                            if (isAdded()) {
                                unspentOutputTextView.setText(
                                        String.format(Locale.getDefault(), "%f QTUM", unspentOutputs.get(0).getAmount())
                                );
                            }
                        }
                    });
        } catch (UnreadableWalletException e) {
            ((MainActivity) getActivity()).displayError(LOG_TAG, e.getMessage());
        }
    }
}