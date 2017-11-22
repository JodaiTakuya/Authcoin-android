package com.authcoinandroid.service.qtum.mapper;

import com.authcoinandroid.model.EntityIdentityRecord;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;

import java.util.ArrayList;
import java.util.List;

public class RecordContractParamMapper {
    public static List<Type> resolveEirContractParams(EntityIdentityRecord eir) {
        List<Type> params = new ArrayList<>();
        List<Bytes32> identifiers = new ArrayList<>();
        for (String s : eir.getIdentifiers()) {
            identifiers.add(stringToBytes32(s));
        }
        params.add(new DynamicBytes(eir.getContent().getEncoded()));
        params.add(stringToBytes32(eir.getContentType()));
        params.add(new DynamicArray<>(identifiers));
        params.add(bytesToBytes32(eir.getHash()));
        params.add(new DynamicBytes(eir.getSignature()));
        return params;
    }

    private static Bytes32 stringToBytes32(String string) {
        byte[] byteValue = string.getBytes();
        return bytesToBytes32(byteValue);
    }

    private static Bytes32 bytesToBytes32(byte[] byteValue) {
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }
}