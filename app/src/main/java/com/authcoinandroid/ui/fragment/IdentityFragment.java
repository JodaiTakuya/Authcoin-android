package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.service.WalletService;
import com.authcoinandroid.ui.activity.MainActivity;

public class IdentityFragment extends Fragment {
    private final static String LOG_TAG = "IdentityFragment";

    @BindView(R.id.tv_wallet_address)
    TextView walletAddress;

    public IdentityFragment() {
    }

    @OnClick({R.id.btn_delete_identity})
    void onDeleteWallet(View view) {
        WalletService.getInstance().deleteWallet(this.getContext());
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.INVISIBLE);
        ((MainActivity) getActivity()).applyFragment(WelcomeFragment.class, false);
    }

    private void displayWalletAddress() {
        walletAddress.setText(WalletService.getInstance().getWalletAddress(this.getContext()));
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
        return view;
    }
}
