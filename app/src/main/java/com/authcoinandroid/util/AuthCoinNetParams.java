package com.authcoinandroid.util;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.QtumTestNetParams;

public final class AuthCoinNetParams {

    private AuthCoinNetParams() {
    }

    public static NetworkParameters getNetParams() {
        return QtumTestNetParams.get();
    }

    public static String getUrl() {
        return "http://145.239.197.39:5931/";
    }
}
