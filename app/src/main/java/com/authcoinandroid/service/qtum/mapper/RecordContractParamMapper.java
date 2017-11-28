package com.authcoinandroid.service.qtum.mapper;

import com.authcoinandroid.model.EntityIdentityRecord;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.authcoinandroid.util.ContractUtil.bytesToBytes32;
import static com.authcoinandroid.util.ContractUtil.stringToBytes32;
import static org.web3j.abi.Utils.convert;

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

    public static EntityIdentityRecord resolveEirFromAbiReturn(String abiReturn) {
        // let god have mercy on my soul
        List<TypeReference<?>> outputParameters = Arrays.asList(
                new TypeReference<Bytes32>() { // id
                }, new TypeReference<Uint256>() { // blockNumber
                }, new TypeReference<DynamicBytes>() { // content
                }, new TypeReference<Bytes32>() { // contentType
                }, new TypeReference<Bool>() { // revoked
                }, new TypeReference<DynamicArray<Bytes32>>() { // identifiers
                }, new TypeReference<Bytes32>() { // hash
                }, new TypeReference<DynamicBytes>() { // signature
                });
        List<Type> output = FunctionReturnDecoder.decode(abiReturn, convert(outputParameters));
        EntityIdentityRecord eir = new EntityIdentityRecord();
        eir.setIdentifiers(getIdentifiers((List<Bytes32>) output.get(5).getValue()));
        eir.setContentType(new String((byte[]) output.get(3).getValue(), StandardCharsets.UTF_8));
        eir.setHash((byte[]) output.get(6).getValue());
        eir.setSignature((byte[]) output.get(7).getValue());
        return eir;
    }

    private static String[] getIdentifiers(List<Bytes32> ids) {
        List<String> identifiers = new ArrayList<>();
        for (Bytes32 identifier : ids) {
            identifiers.add(new String(identifier.getValue(), StandardCharsets.UTF_8));
        }
        return identifiers.toArray(new String[0]);
    }
}