package com.authcoinandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.identity.EirRepository;
import com.authcoinandroid.service.identity.IdentityService;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.util.ButterKnifeUtil;

import org.spongycastle.util.encoders.Base64;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class EirFragment extends Fragment {
    private final static String LOG_TAG = "EirFragment";

    @BindViews({
            R.id.tv_eir_identifiers_label,
            R.id.tv_eir_content_label,
            R.id.tv_eir_content_type_label,
            R.id.tv_eir_hash_label,
            R.id.tv_eir_signature_label
    })
    List<TextView> labelViews;

    @BindView(R.id.tv_alias)
    TextView alias;

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
        @Override
        public void apply(View view, int index) {
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
        IdentityService identityService = IdentityService.getInstance(getActivity().getApplication());
        View view = inflater.inflate(R.layout.eir_fragment, container, false);
        Bundle bundle = this.getArguments();
        byte[] eirId = bundle.getByteArray("eir");
        displayEir(eirId);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }

    private void displayEir(byte[] eirId) {
        try {
            ((AuthCoinApplication) getActivity().getApplication()).getEirRepository().find(eirId)

                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<EntityIdentityRecord>() {
                        @Override
                        public void onComplete() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(LOG_TAG, e.getMessage());
                            ButterKnife.apply(labelViews, ButterKnifeUtil.SET_INVISIBLE);
                        }

                        @Override
                        public void onNext(EntityIdentityRecord eir) {
                            mapEirToTextViews(eir);
                            Log.d(LOG_TAG, eir.toString());
                        }
                    });
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void mapEirToTextViews(EntityIdentityRecord eir) {
        alias.setText(eir.getKeyStoreAlias());
        identifiers.setText(TextUtils.join(", ", eir.getIdentifiers()));
        content.setText(Base64.toBase64String(eir.getContent()));
        contentType.setText(eir.getContentType());
        hash.setText(Base64.toBase64String(eir.getHash()));
        signature.setText(Base64.toBase64String(eir.getSignature()));
    }

}
