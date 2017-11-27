package com.authcoinandroid.util;

import org.web3j.abi.datatypes.generated.Bytes32;

public class ContractUtil {
    public static Bytes32 stringToBytes32(String string) {
        return bytesToBytes32(string.getBytes());
    }

    public static Bytes32 bytesToBytes32(byte[] byteValue) {
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }

    public static String stripLeadingZeroes(String s) {
        return s.replaceAll("^0*", "");
    }
}