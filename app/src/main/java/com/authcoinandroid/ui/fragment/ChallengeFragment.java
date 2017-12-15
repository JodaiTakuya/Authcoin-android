package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.ui.activity.MainActivity;
import com.authcoinandroid.util.AndroidUtil;

import java.util.List;

import static com.authcoinandroid.model.AssetBlockChainStatus.MINED;

public class ChallengeFragment extends Fragment {
    private final static String LOG_TAG = "ChallengeFragment";

    @BindView(R.id.btn_new_challenge)
    FloatingActionButton newChallengeButton;

    public ChallengeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_fragment, container, false);
        ButterKnife.bind(this, view);

        AuthCoinApplication application = (AuthCoinApplication) getActivity().getApplication();
        List<EntityIdentityRecord> minedIdentities = application.getEirRepository().findByStatus(MINED);
        newChallengeButton.setOnClickListener(addChallengeListener(!minedIdentities.isEmpty()));

        return view;
    }

    private View.OnClickListener addChallengeListener(boolean minedEirsExist) {
        return listener -> {
            if (!minedEirsExist) {
                AndroidUtil.displayNotification(getContext(), getString(R.string.missing_identities_for_challenge));
            } else {
                ((MainActivity) getActivity()).applyFragment(NewChallengeFragment.class, true, true);
            }
        };
    }

}
