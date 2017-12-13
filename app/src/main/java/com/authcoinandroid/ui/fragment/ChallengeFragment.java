package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.ui.activity.MainActivity;
import com.authcoinandroid.util.AndroidUtil;

import java.util.List;

import static com.authcoinandroid.model.AssetBlockChainStatus.MINED;

public class ChallengeFragment extends Fragment {
    private final static String LOG_TAG = "ChallengeFragment";

    public ChallengeFragment() {
    }

    @OnClick({R.id.btn_new_challenge})
    void onOpenAddChallenge(View view) {
        AuthCoinApplication application = (AuthCoinApplication) getActivity().getApplication();
        List<EntityIdentityRecord> minedIdentities = application.getEirRepository().findByStatus(MINED);

        // Disallow sending Challenges if user has no mined EIR-s
        if (minedIdentities.isEmpty()) {
            AndroidUtil.displayNotification(getContext(), getString(R.string.missing_identities_for_challenge));
            return;
        }

        ((MainActivity) getActivity()).applyFragment(NewChallengeFragment.class, true, true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
