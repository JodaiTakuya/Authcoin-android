package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.authcoinandroid.R;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.module.challenges.Challenges;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.util.AndroidUtil;
import com.authcoinandroid.util.Util;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

import static com.authcoinandroid.model.AssetBlockChainStatus.MINED;

public class NewChallengeFragment extends Fragment {
    private final static String LOG_TAG = "NewChallengeFragment";

    @BindView(R.id.s_eir_verifier)
    Spinner verifierEir;
    @BindView(R.id.et_eir_target)
    TextView targetEirId;
    @BindView(R.id.s_challenge_type)
    Spinner challengeType;

    public NewChallengeFragment() {
    }

    @OnClick({R.id.btn_send_challenge})
    void onSendChallenge(View view) {
        EntityIdentityRecord verifier = (EntityIdentityRecord) verifierEir.getSelectedItem();
        EntityIdentityRecord target = getEirById(String.valueOf(targetEirId.getText()));
        Challenge challenge = Challenges.get(String.valueOf(challengeType.getSelectedItem()));

        ChallengeRecord challengeRecord = createChallengeRecord(challenge, verifier, target);

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
        ArrayAdapter<EntityIdentityRecord> verifierEirAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, minedIdentities);
        verifierEirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        verifierEir.setAdapter(verifierEirAdapter);

        List<String> challengeTypes = new ArrayList<>(Challenges.getAllTypes());
        ArrayAdapter<String> challengeTypeAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, challengeTypes);
        challengeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        challengeType.setAdapter(challengeTypeAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }

    private ChallengeRecord createChallengeRecord(Challenge challenge, EntityIdentityRecord verifier, EntityIdentityRecord target) {
        byte[] id = Util.generateId();
        byte[] vaeId = Util.generateId();
        return new ChallengeRecord(id, vaeId, challenge.getType(), challenge.getContent(), verifier, target);
    }

    private EntityIdentityRecord getEirById(String eirId) {
        final EntityIdentityRecord[] result = new EntityIdentityRecord[1];

        ((AuthCoinApplication) getActivity().getApplication()).getIdentityService()
                .getEirById(eirId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<EntityIdentityRecord>() {
                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG, "getEirById complete!");
                    }

                    @Override
                    public void onNext(EntityIdentityRecord eir) {
                        result[0] = eir;
                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidUtil.displayNotification(getContext(), e.getMessage());
                        Log.d(LOG_TAG, e.getMessage());
                    }
                });

        return result[0];
    }
}
