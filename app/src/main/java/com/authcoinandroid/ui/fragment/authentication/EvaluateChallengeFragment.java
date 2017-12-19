package com.authcoinandroid.ui.fragment.authentication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.authcoinandroid.R;
import com.authcoinandroid.model.ChallengeRecord;
import org.spongycastle.util.encoders.Hex;

public class EvaluateChallengeFragment extends Fragment {

    private View.OnClickListener listener;
    private TextView challengeType;
    private TextView challengeContent;
    private Button approveButton;
    private ChallengeRecord challenge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_fragment_evaluate_challenge, container, false);

        challengeType = (TextView) view.findViewById(R.id.tv_challenge_type);
        challengeContent = (TextView) view.findViewById(R.id.tv_challenge_content);

        challengeType.setText(challenge.getType());
        challengeContent.setText(Hex.toHexString(challenge.getChallenge()));

        approveButton = (Button) view.findViewById(R.id.approve_challenge);
        approveButton.setOnClickListener(listener);

        return view;
    }

    public boolean isApproved() {
        return true;
    }

    public void setApproveButtonListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setChallengeRecord(ChallengeRecord cr) {
        this.challenge = cr;
    }
}
