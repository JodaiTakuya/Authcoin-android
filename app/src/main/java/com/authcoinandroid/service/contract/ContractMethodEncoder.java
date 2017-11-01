package com.authcoinandroid.service.contract;

import com.authcoinandroid.model.contract.ContractMethodParameter;
import com.authcoinandroid.util.crypto.Keccak;
import com.authcoinandroid.util.crypto.Parameters;
import org.bitcoinj.core.Base58;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ContractMethodEncoder {
    private final String ARRAY_PARAMETER_TYPE = "(.*?\\d+)\\[(\\d*)\\]";

    private final static String ARRAY_PARAMETER_CHECK_PATTERN = ".*?\\d+\\[\\d*\\]";
    private String hashPattern = "0000000000000000000000000000000000000000000000000000000000000000"; //64b
    private final static String TYPE_INT = "int";
    private final static String TYPE_STRING = "string";
    private final static String TYPE_ADDRESS = "address";
    private final static String TYPE_BOOL = "bool";
    private long currentStringOffset = 0;
    private int radix = 16;

    String encodeMethodCall(String methodName) {
        return encodeParamsForMethod(methodName, null);
    }

    String encodeParamsForMethod(String methodName, final List<ContractMethodParameter> contractMethodParameterList) {
        StringBuilder parameters = new StringBuilder();
        StringBuilder abiParams = new StringBuilder();
        if (contractMethodParameterList != null && contractMethodParameterList.size() != 0) {
            for (ContractMethodParameter parameter : contractMethodParameterList) {
                abiParams.append(convertParameter(parameter));
                parameters.append(parameter.getType()).append(",");
            }
            methodName = methodName + "(" + parameters.substring(0, parameters.length() - 1) + ")";
        } else {
            methodName = methodName + "()";
        }
        Keccak keccak = new Keccak();
        String hashMethod = keccak.getHash(Hex.toHexString((methodName).getBytes()), Parameters.KECCAK_256).substring(0, 8);
        abiParams.insert(0, hashMethod);
        return abiParams.toString();
    }


    private boolean parameterIsArray(ContractMethodParameter contractMethodParameter) {
        Pattern p = Pattern.compile(ARRAY_PARAMETER_CHECK_PATTERN);
        Matcher m = p.matcher(contractMethodParameter.getType());
        return m.matches();
    }

    private String convertParameter(ContractMethodParameter parameter) {
        String value = parameter.getValue();

        if (parameterIsArray(parameter)) {
            return getStringOffset(parameter);
        } else {
            if (parameter.getType().contains(TYPE_INT)) {
                return appendNumericPattern(convertToByteCode(new BigInteger(value)));
            } else if (parameter.getType().contains(TYPE_STRING)) {
                return getStringOffset(parameter);
            } else if (parameter.getType().contains(TYPE_ADDRESS) && value.length() == 34) {
                String hexString = Hex.toHexString(Base58.decode(value));
                return appendAddressPattern(hexString.substring(2, 42));
            } else if (parameter.getType().contains(TYPE_ADDRESS)) {
                return getStringOffset(parameter);
            } else if (parameter.getType().contains(TYPE_BOOL)) {
                return appendBoolean(value);
            }
        }

        return "";
    }

    private String convertToByteCode(BigInteger value) {
        return value.toString(radix);
    }

    private String convertToByteCode(long value) {
        return Long.toString(value, radix);
    }

    private String appendNumericPattern(String value) {
        return hashPattern.substring(0, hashPattern.length() - value.length()) + value;
    }

    private String appendAddressPattern(String value) {
        return hashPattern.substring(value.length()) + value;
    }

    private String appendBoolean(String parameter) {
        return Boolean.valueOf(parameter) ? appendNumericPattern("1") : appendNumericPattern("0");
    }

    private String getStringOffset(ContractMethodParameter parameter) {
        long currOffset = currentStringOffset * 32;
        currentStringOffset = getStringHash(parameter.getValue()).length() / hashPattern.length() + 1/*string length section*/;
        return appendNumericPattern(convertToByteCode(currOffset));
    }

    private String getStringHash(String value) {
        if (value.length() <= hashPattern.length()) {
            return formNotFullString(value);
        } else {
            int ost = value.length() % hashPattern.length();
            return value + hashPattern.substring(0, hashPattern.length() - ost);
        }
    }

    private String formNotFullString(String value) {
        return value + hashPattern.substring(value.length());
    }
}