package com.authcoinandroid.ui.fragment.authentication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.BindView;
import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.identity.EirRepository;
import com.authcoinandroid.ui.AuthCoinApplication;

import java.util.List;

public class EirSelectorFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button startAuthentication;
    private ArrayAdapter<EntityIdentityRecord> eirAdapter;
    private Spinner eirSpinner;
    private View.OnClickListener nextButtonListener;
    private EntityIdentityRecord selectedEir;
    private TextView applicationName;
    private TextView applicationUrl;

    @BindView(R.id.tv_app_name)
    TextView appName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_fragment_select_eir, container, false);

        EirRepository eirRepository = ((AuthCoinApplication) getActivity().getApplication()).getEirRepository();
        List<EntityIdentityRecord> eirs = eirRepository.findAll();
        selectedEir = eirs.get(0);

        eirAdapter = new ArrayAdapter<>(this.getContext(),  android.R.layout.simple_spinner_item, eirs);
        eirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eirSpinner = (Spinner) view.findViewById(R.id.s_identity);
        eirSpinner.setAdapter(eirAdapter);
        eirSpinner.setOnItemSelectedListener(this);

        startAuthentication = (Button) view.findViewById(R.id.authenticate);
        startAuthentication.setOnClickListener(nextButtonListener);

        Uri uri = getActivity().getIntent().getData();
        applicationName = (TextView) view.findViewById(R.id.tv_app_name);
        applicationUrl = (TextView) view.findViewById(R.id.tv_app_url);
        applicationName.setText("" + uri.getQueryParameter("appName"));
        applicationUrl.setText("" + uri.getQueryParameter("serverUrl"));

        String serverSessionId = uri.getQueryParameter("sessionId");
        String serverEir = uri.getQueryParameter("serverEir");

        return view;
    }

    public void setNextButtonListener(View.OnClickListener o) {
        this.nextButtonListener = o;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.selectedEir = eirAdapter.getItem(position);
    }

    public EntityIdentityRecord getSelectedEir() {
        return selectedEir;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}


