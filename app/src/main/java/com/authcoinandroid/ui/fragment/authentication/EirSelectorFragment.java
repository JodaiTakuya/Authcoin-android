package com.authcoinandroid.ui.fragment.authentication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.identity.EirRepository;
import com.authcoinandroid.ui.AuthCoinApplication;

import java.util.List;

import butterknife.ButterKnife;

public class EirSelectorFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button startAuthentication;
    private View.OnClickListener nextButtonListener;

    private Spinner eirSpinner;
    private ArrayAdapter<EntityIdentityRecord> eirAdapter;

    private EntityIdentityRecord selectedEir;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EirRepository eirRepository = ((AuthCoinApplication) getActivity().getApplication()).getEirRepository();
        List<EntityIdentityRecord> eirs = eirRepository.findAll();

        selectedEir = eirs.get(0);
        eirAdapter = new ArrayAdapter<EntityIdentityRecord>(this.getContext(), 0, eirs) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createView(position, convertView, parent);
            }

            @NonNull
            private View createView(int pos, View v, ViewGroup group) {
                EntityIdentityRecord eir = getItem(pos);

                // Check if an existing view is being reused, otherwise inflate the view
                if (v == null) {
                    v = LayoutInflater.from(getContext()).inflate(R.layout.eir_identifier, group, false);
                }
                TextView eirName = (TextView) v.findViewById(R.id.eirIdentifierName);
                eirName.setText(eir.getIdentifiers().get(0).getValue());
                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createView(position, convertView, parent);
            }
        };
        eirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        View view = inflater.inflate(R.layout.aa_fragment_select_eir, container, false);

        eirSpinner = (Spinner) view.findViewById(R.id.verifier_eir_selector);
        startAuthentication = (Button) view.findViewById(R.id.authenticate);
        startAuthentication.setOnClickListener(nextButtonListener);

        eirSpinner.setAdapter(eirAdapter);
        eirSpinner.setOnItemSelectedListener(this);
        ButterKnife.bind(this, view);

        return view;
    }

    public void setNextButtonListener(View.OnClickListener o) {
        this.nextButtonListener = o;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.selectedEir = eirAdapter.getItem(position);

        Log.i("TODO", "EIR selected");
    }

    public EntityIdentityRecord getSelectedEir() {
        return selectedEir;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
