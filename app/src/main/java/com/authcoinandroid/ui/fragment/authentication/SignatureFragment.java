package com.authcoinandroid.ui.fragment.authentication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.authcoinandroid.R;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.Challenges;
import org.spongycastle.util.encoders.Hex;

public class SignatureFragment extends Fragment {

    private View.OnClickListener signCallback;
    private ChallengeResponseRecord challengeResponse;

    private TextView challengeResponseView;
    private EditText lifespan;

    private TextView validSignature;

    private Button signButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_fragment_signature, container, false);

        challengeResponseView = (TextView) view.findViewById(R.id.tv_challenge_response);

        validSignature = (TextView) view.findViewById(R.id.tv_valid_siganture);
        lifespan = (EditText) view.findViewById(R.id.et_lifespan);

        ChallengeRecord challengeRecord = challengeResponse.getChallenge();
        EntityIdentityRecord targetEir = challengeRecord.getTarget();
        boolean isValid = Challenges.getVerifier(challengeRecord.getType()).verify(targetEir, challengeRecord.getChallenge(), challengeResponse.getResponse());

        validSignature.setText("" + isValid);
        challengeResponseView.setText(Hex.toHexString(challengeResponse.getResponse()));

        signButton = (Button) view.findViewById(R.id.sign_button);
        signButton.setOnClickListener(signCallback);

        return view;
    }

    public int getLifespan() {
        // TODO read lifespan value
        return Integer.valueOf(365);
    }

    public void setApproveSignatureListener(View.OnClickListener signCallback) {
        this.signCallback = signCallback;
    }

    public void setChallengeResponse(ChallengeResponseRecord challengeResponse) {
        this.challengeResponse = challengeResponse;
    }
}
