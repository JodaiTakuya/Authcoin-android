package com.authcoinandroid.ui.fragment.authentication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.authcoinandroid.R;
import com.authcoinandroid.module.challenges.Challenges;

import java.util.ArrayList;
import java.util.Set;

public class ChallengeTypeSelectorFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button selectChallengeButton;
    private Spinner challengeSpinner;
    private ArrayAdapter<String> challengeAdapter;
    private View.OnClickListener selectChallengeButtonListener;
    private String selectedChallenge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Set<String> types = Challenges.getAllTypes();

        challengeAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(types));
        challengeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        View view = inflater.inflate(R.layout.aa_fragment_select_challenge, container, false);

        challengeSpinner = (Spinner) view.findViewById(R.id.verifier_challenge_selector);
        challengeSpinner.setAdapter(challengeAdapter);
        challengeSpinner.setOnItemSelectedListener(this);

        selectChallengeButton = (Button) view.findViewById(R.id.selectChallenge);
        selectChallengeButton.setOnClickListener(selectChallengeButtonListener);

        return view;
    }

    public void setSelectChallengeButtonListener(View.OnClickListener selectChallengeButtonListener) {
        this.selectChallengeButtonListener = selectChallengeButtonListener;
    }

    public String getSelectedChallenge() {
        return selectedChallenge;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.selectedChallenge = challengeAdapter.getItem(position);
/*
        synchronized (VAProcessRunnable.lock) {
            VAProcessRunnable.queue.add(new ChallengeTypeMessageResponse(challenge));
            VAProcessRunnable.lock.notify();
        }

        */
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
