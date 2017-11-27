package com.authcoinandroid.service.qtum.mapper;

import com.authcoinandroid.model.EntityIdentityRecord;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.authcoinandroid.util.ContractUtil.bytesToBytes32;
import static com.authcoinandroid.util.ContractUtil.stringToBytes32;
import static java.util.Collections.emptyList;

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
                new TypeReference<Bytes32>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<DynamicBytes>() {
                }, new TypeReference<Bytes32>() {
                }, new TypeReference<Bool>() {
                }, new TypeReference<DynamicArray<Bytes32>>() {
                }, new TypeReference<Bytes32>() {
                }, new TypeReference<DynamicBytes>() {
                });
        Function function = new Function("", emptyList(), outputParameters);
        List<Type> output = FunctionReturnDecoder.decode(abiReturn, function.getOutputParameters());
        EntityIdentityRecord eir = new EntityIdentityRecord();
        eir.setContentType(new String((byte[]) output.get(3).getValue()));
        return eir;
    }
}