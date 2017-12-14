package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.ui.AuthCoinApplication;

import java.util.List;

import static com.authcoinandroid.model.AssetBlockChainStatus.MINED;

public class NewChallengeFragment extends Fragment {
    private final static String LOG_TAG = "NewChallengeFragment";

    public NewChallengeFragment() {
    }

    @OnClick({R.id.btn_send_challenge})
    void onSendChallenge(View view) {
        // TODO Add any missing fields required for Challenge registration
        // TODO (AuthCoinApplication) getActivity().getApplication()).getChallengeService().registerChallenge(...)
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_challenge_fragment, container, false);
        ButterKnife.bind(this, view);

        AuthCoinApplication application = (AuthCoinApplication) getActivity().getApplication();
        List<EntityIdentityRecord> minedIdentities = application.getEirRepository().findByStatus(MINED);

        // Uses the toString method of EntityIdentityRecord to display value in ArrayAdapter
        Spinner verifierEir = (Spinner) view.findViewById(R.id.s_eir_verifier);
        ArrayAdapter<EntityIdentityRecord> dataAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, minedIdentities);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        verifierEir.setAdapter(dataAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }
}
