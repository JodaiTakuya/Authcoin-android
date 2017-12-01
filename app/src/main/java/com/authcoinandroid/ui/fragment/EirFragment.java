package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import com.authcoinandroid.R;
import com.authcoinandroid.exception.GetEirException;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.identity.IdentityService;
import com.authcoinandroid.util.ContractUtil;
import org.spongycastle.util.encoders.Base64;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;

public class EirFragment extends Fragment {
    private final static String LOG_TAG = "EirFragment";

    private String alias;
    private EntityIdentityRecord eir;

    @BindViews({
            R.id.tv_eir_identifiers_label,
            R.id.tv_eir_content_label,
            R.id.tv_eir_content_type_label,
            R.id.tv_eir_hash_label,
            R.id.tv_eir_signature_label
    })
    List<TextView> labelViews;

    @BindView(R.id.tv_alias)
    TextView tv_alias;

    @BindView(R.id.tv_eir_identifiers)
    TextView identifiers;

    @BindView(R.id.tv_eir_content)
    TextView content;

    @BindView(R.id.tv_eir_content_type)
    TextView contentType;

    @BindView(R.id.tv_eir_hash)
    TextView hash;

    @BindView(R.id.tv_eir_signature)
    TextView signature;

    static final ButterKnife.Action<View> SET_INVISIBLE = new ButterKnife.Action<View>() {
        @Override public void apply(View view, int index) {
            view.setVisibility(View.INVISIBLE);
        }
    };

    public EirFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.eir_fragment, container, false);

        Bundle bundle = this.getArguments();
        alias = bundle.getString("alias");
        getEir();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }

    private void getEir() {
        try {
            IdentityService.getInstance().getEir(alias)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<EntityIdentityRecord>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(LOG_TAG, e.getMessage());
                            ButterKnife.apply(labelViews, SET_INVISIBLE);
                        }

                        @Override
                        public void onNext(EntityIdentityRecord responseEir) {
                            eir = responseEir;
                            mapEirToTextViews();
                            Log.d(LOG_TAG, Arrays.toString(eir.getIdentifiers()));
                        }
                    });
        } catch (GetEirException e) {
            Log.e("EirAliasAdapter", e.getMessage());
        }
    }

    private void mapEirToTextViews() {
        tv_alias.setText(alias);
        identifiers.setText(TextUtils.join(", ", eir.getIdentifiers()));
        content.setText(ContractUtil.getEirIdAsString(eir.getContent()));
        contentType.setText(eir.getContentType());
        hash.setText(Base64.toBase64String(eir.getHash()));
        signature.setText(Base64.toBase64String(eir.getSignature()));
    }

}
