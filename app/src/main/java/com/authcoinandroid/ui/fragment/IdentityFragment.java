package com.authcoinandroid.ui.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.contract.AuthcoinContractService;
import com.authcoinandroid.service.identity.IdentityService;
import com.authcoinandroid.service.identity.WalletService;
import com.authcoinandroid.service.qtum.model.UnspentOutput;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.ui.activity.MainActivity;
import com.authcoinandroid.ui.activity.WelcomeActivity;
import com.authcoinandroid.ui.adapter.EirAdapter;
import com.authcoinandroid.util.AndroidUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IdentityFragment extends Fragment {
    private final static String LOG_TAG = "IdentityFragment";
    @BindView(R.id.iv_wallet_copy)
    ImageView walletAddressCopy;
    @BindView(R.id.tv_unspent_output)
    TextView unspentOutput;
    @BindView(R.id.swipe_refresh_eirs)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.lv_eirs)
    ListView eirList;
    private List<EntityIdentityRecord> eirs;

    public IdentityFragment() {
    }

    @OnLongClick({R.id.iv_wallet})
    boolean onDeleteWallet(View view) {
        WalletService.getInstance().deleteWallet(this.getContext());
        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        startActivity(intent);
        return true;
    }

    @OnClick({R.id.iv_wallet_copy})
    void onCopyWalletAddress(View view) {
        ClipboardManager clipboard = (ClipboardManager) this.getContext().getSystemService(Activity.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Wallet address", walletAddressCopy.getContentDescription()));
        AndroidUtil.displayNotification(getContext(), getString(R.string.wallet_address_copied));
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
        populateEirList();

        AuthCoinApplication application = (AuthCoinApplication) getActivity().getApplication();
        IdentityService identityService = application.getIdentityService();

        // TODO This could probably be optimized by not repopulating the whole list
        swipeRefreshLayout.setOnRefreshListener(() -> Observable.fromIterable(eirs)
                .flatMap(eir -> identityService.updateEirStatusFromBc(eir).toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onComplete() {
                        swipeRefreshLayout.setRefreshing(false);
                        populateEirList();
                    }

                    @Override
                    public void onNext(Object o) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }));

        return view;
    }

    private void populateEirList() {
        try {
            IdentityService identityService = ((AuthCoinApplication) getActivity().getApplication()).getIdentityService();

            eirs = new ArrayList<>();
            eirs.addAll(identityService.getAll());

            EirAdapter adapter = new EirAdapter(getContext(), eirs);
            eirList.setAdapter(adapter);

            eirList.setOnItemClickListener((parent, view, position, id) -> {
                EntityIdentityRecord eir = eirs.get(position);
                Bundle bundle = new Bundle();
                bundle.putByteArray("eir", eir.getId());

                if (getContext() instanceof MainActivity) {
                    ((MainActivity) getContext()).applyFullFragmentWithBundle(EirFragment.class, bundle);
                }
            });
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }

    private void attachWalletAddressToCopyImage() {
        try {
            String walletAddress = WalletService.getInstance().getWalletAddress(this.getContext());
            Log.d(LOG_TAG, "Wallet address is: " + walletAddress);
            walletAddressCopy.setContentDescription(walletAddress);
        } catch (UnreadableWalletException e) {
            Log.e(LOG_TAG, "Unable to load wallet address: " + e.getMessage());
            AndroidUtil.displayNotification(getContext(), e.getMessage());
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
                                unspentOutput.setText(getString(R.string.missing_value));
                            }
                        }

                        @Override
                        public void onNext(List<UnspentOutput> unspentOutputs) {
                            if (isAdded()) {
                                BigDecimal sum = BigDecimal.ZERO;
                                for (UnspentOutput unspentOutput : unspentOutputs) {
                                    sum = sum.add(unspentOutput.getAmount());
                                }
                                unspentOutput.setText(
                                        String.format(Locale.getDefault(), "%f QTUM", sum)
                                );
                            }
                        }
                    });
        } catch (UnreadableWalletException e) {
            AndroidUtil.displayNotification(getContext(), e.getMessage());
        }
    }
}