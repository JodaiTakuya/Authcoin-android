package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.authcoinandroid.R;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.ui.activity.MainActivity;
import com.authcoinandroid.ui.adapter.ChallengeAdapter;
import com.authcoinandroid.util.AndroidUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

import static com.authcoinandroid.model.AssetBlockChainStatus.MINED;

public class ChallengeFragment extends Fragment {
    private final static String LOG_TAG = "ChallengeFragment";

    @BindView(R.id.s_eir)
    Spinner currentEir;
    @BindView(R.id.btn_new_challenge)
    FloatingActionButton newChallengeButton;
    @BindView(R.id.lv_challenges)
    ListView challengeList;

    private List<ChallengeRecord> challengeRecords;

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
        currentEir.setEnabled(!minedIdentities.isEmpty());

        // Uses the toString method of EntityIdentityRecord to display value in ArrayAdapter
        ArrayAdapter<EntityIdentityRecord> currentEirAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.spinner_item_light, minedIdentities);
        currentEirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentEir.setAdapter(currentEirAdapter);
        currentEir.setOnItemSelectedListener(changeCurrentEirListener());

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

    private AdapterView.OnItemSelectedListener changeCurrentEirListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populateChallengeList((EntityIdentityRecord) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    private void populateChallengeList(EntityIdentityRecord currentEir) {
        try {
            getChallengeRecordsForEir(currentEir);

            if (challengeRecords != null) {
                ChallengeAdapter adapter = new ChallengeAdapter(getContext(), challengeRecords);
                challengeList.setAdapter(adapter);

                challengeList.setOnItemClickListener((parent, view, position, id) -> {
                    ChallengeRecord challengeRecord = challengeRecords.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("challengeRecord", challengeRecord.getId());

                    if (getContext() instanceof MainActivity) {
                        // TODO Open a new Fragment (perhaps separate one required for sent/received challenges?)
                        // ((MainActivity) getContext()).applyFullFragmentWithBundle(/* CrFragment */, bundle);
                    }
                });
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }

    private void getChallengeRecordsForEir(EntityIdentityRecord eir) {
        List<ChallengeRecord> newChallengeRecords = new ArrayList<>();

        ((AuthCoinApplication) getActivity().getApplication()).getChallengeService()
                .getChallengeRecordsForEir(eir)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<ChallengeRecord>>() {
                    @Override
                    public void onComplete() {
                        challengeRecords = new ArrayList<>();
                        challengeRecords.addAll(newChallengeRecords);
                    }

                    @Override
                    public void onNext(List<ChallengeRecord> nextChallengeRecords) {
                        newChallengeRecords.addAll(nextChallengeRecords);
                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidUtil.displayNotification(getContext(), e.getMessage());
                        Log.d(LOG_TAG, e.getMessage());
                    }
                });
    }
}
