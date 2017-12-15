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
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.authcoinandroid.R;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.ui.activity.MainActivity;
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
    Spinner currentEirSpinner;
    @BindView(R.id.btn_new_challenge)

    FloatingActionButton newChallengeButton;

    private AdapterView.OnItemSelectedListener eirSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            getChallengeRecordsForEir((EntityIdentityRecord) parent.getItemAtPosition(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

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
        currentEirSpinner.setEnabled(!minedIdentities.isEmpty());

        // Uses the toString method of EntityIdentityRecord to display value in ArrayAdapter
        ArrayAdapter<EntityIdentityRecord> dataAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.spinner_item_light, minedIdentities);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentEirSpinner.setAdapter(dataAdapter);

        currentEirSpinner.setOnItemSelectedListener(eirSpinnerListener);

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

    private void getChallengeRecordsForEir(EntityIdentityRecord eir) {
        List<ChallengeRecord> allChallengeRecords = new ArrayList<>();

        ((AuthCoinApplication) getActivity().getApplication()).getChallengeService()
                .getChallengeRecordsForEir(eir)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<ChallengeRecord>>() {
                    @Override
                    public void onComplete() {
                        // TODO populate the ListView with allChallengeRecords
                    }

                    @Override
                    public void onNext(List<ChallengeRecord> challengeRecords) {
                        allChallengeRecords.addAll(challengeRecords);
                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidUtil.displayNotification(getContext(), e.getMessage());
                        Log.d(LOG_TAG, e.getMessage());
                    }
                });
    }
}
