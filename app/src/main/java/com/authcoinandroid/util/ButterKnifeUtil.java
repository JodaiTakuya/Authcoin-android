package com.authcoinandroid.util;

import android.support.annotation.NonNull;
import android.view.View;
import butterknife.ButterKnife;

public class ButterKnifeUtil {

    public static ButterKnife.Action<View> SET_INVISIBLE = new ButterKnife.Action<View>() {
        @Override public void apply(@NonNull View view, int index) {
            view.setVisibility(View.INVISIBLE);
        }
    };
}
