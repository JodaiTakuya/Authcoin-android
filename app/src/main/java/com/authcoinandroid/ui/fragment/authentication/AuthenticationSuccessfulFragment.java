package com.authcoinandroid.ui.fragment.authentication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.authcoinandroid.R;
import com.authcoinandroid.module.messaging.UserAuthenticatedMessage;

import org.spongycastle.util.encoders.Hex;

public class AuthenticationSuccessfulFragment extends Fragment {

    private UserAuthenticatedMessage result;
    private TextView targetCr;
    private TextView verifierCr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_fragment_authentication_successful, container, false);

        targetCr = (TextView) view.findViewById(R.id.tv_target_cr);
        verifierCr = (TextView) view.findViewById(R.id.tv_verifier_cr);

        targetCr.setText(Hex.toHexString(result.getTargetChallengeRecord().getId()));
        verifierCr.setText(Hex.toHexString(result.getVerifierChallengeRecord().getId()));
        return view;
    }

    public void setResult(UserAuthenticatedMessage result) {
        this.result = result;
    }
}
