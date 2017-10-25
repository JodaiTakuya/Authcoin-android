package com.authcoinandroid.util;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.QtumTestNetParams;

public class AuthCoinNetParams {
    public static NetworkParameters getNetParams() {
        return QtumTestNetParams.get();
    }

    public static String getUrl() {
        return "http://163.172.251.4:5931/";
    }
}
