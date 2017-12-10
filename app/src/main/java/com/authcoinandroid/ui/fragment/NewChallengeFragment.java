package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import butterknife.ButterKnife;
import com.authcoinandroid.R;

import java.util.ArrayList;
import java.util.List;

public class NewChallengeFragment extends Fragment {
    private final static String LOG_TAG = "NewChallengeFragment";

    public NewChallengeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_challenge_fragment, container, false);
        ButterKnife.bind(this, view);

        List<String> identities = new ArrayList<>();
        identities.add("Test1");
        identities.add("Test2");

        Spinner verifierEir = (Spinner) view.findViewById(R.id.s_eir_verifier);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, identities);
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
