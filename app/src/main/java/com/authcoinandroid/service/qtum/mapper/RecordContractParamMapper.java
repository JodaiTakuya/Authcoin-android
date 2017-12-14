package com.authcoinandroid.service.qtum.mapper;

import com.authcoinandroid.model.BaseEirIdentifier;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EirIdentifier;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.identity.IdentityService;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.authcoinandroid.util.ContractUtil.bytesToBytes32;
import static com.authcoinandroid.util.ContractUtil.stringToBytes32;
import static com.authcoinandroid.util.crypto.CryptoUtil.toPublicKey;
import static org.web3j.abi.Utils.convert;

public class RecordContractParamMapper {

    public static List<Type> resolveEirContractParams(EntityIdentityRecord eir) {
        List<Type> params = new ArrayList<>();
        List<Bytes32> identifiers = new ArrayList<>();
        for (BaseEirIdentifier s : eir.getIdentifiers()) {
            identifiers.add(stringToBytes32(s.getValue()));
        }
        params.add(new DynamicBytes(eir.getKeyPair().getPublic().getEncoded()));
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
        // TODO check if blockchain EIR and EIR in db match
        // TODO: initialise EIR properly: id
        EntityIdentityRecord eir = new EntityIdentityRecord();
        eir.setPublicKey(toPublicKey((byte[]) output.get(2).getValue()));
        eir.setContentType(new String((byte[]) output.get(3).getValue(), StandardCharsets.UTF_8));
        eir.setRevoked(((Bool) output.get(4)).getValue());
        eir.setIdentifiers(getIdentifiers((List<Bytes32>) output.get(5).getValue()));
        eir.setHash((byte[]) output.get(6).getValue());
        eir.setSignature((byte[]) output.get(7).getValue());
        return eir;
    }

    public static ChallengeRecord resolveCrFromAbiReturn(String abiReturn, IdentityService identityService) {
        // TODO parse all data about cr
        List<TypeReference<?>> outputParameters = Arrays.asList(
                new TypeReference<Bytes32>() { // id
                }, new TypeReference<Uint256>() { // block number
                }, new TypeReference<Bytes32>() { // type
                }, new TypeReference<DynamicBytes>() { // challenge
                }, new TypeReference<Address>() { // verifierEir
                }, new TypeReference<Address>() { // targetEir
                }, new TypeReference<Bytes32>() { // hash
                }, new TypeReference<Bytes32>() { // signature
                }
        );
        List<Type> output = FunctionReturnDecoder.decode(abiReturn, convert(outputParameters));
        ChallengeRecord challengeRecord = new ChallengeRecord();
        challengeRecord.setId((byte[]) output.get(0).getValue());
        challengeRecord.setType(new String((byte[]) output.get(2).getValue()));
        challengeRecord.setChallenge((byte[]) output.get(3).getValue());
        challengeRecord.setVerifier(identityService.getEirByAddress((Address) output.get(4)).blockingFirst());
        challengeRecord.setTarget(identityService.getEirByAddress((Address) output.get(5)).blockingFirst());
        challengeRecord.setHash((byte[]) output.get(6).getValue());
        challengeRecord.setSignature((byte[]) output.get(7).getValue());
        return challengeRecord;
    }

    public static List<Address> resolveAddressesFromAbiReturn(String abiReturn) {
        List<TypeReference<?>> outputParameters = Collections.singletonList(
                new TypeReference<DynamicArray<Address>>() {
                });
        List<Type> output = FunctionReturnDecoder.decode(abiReturn, convert(outputParameters));
        return (List<Address>) output.get(0).getValue();
    }

    public static Address resolveAddressFromAbiReturn(String abiReturn) {
        List<TypeReference<?>> outputParameters = Collections.singletonList(
                new TypeReference<Address>() {
                });
        List<Type> output = FunctionReturnDecoder.decode(abiReturn, convert(outputParameters));
        return (Address) output.get(0);
    }

    public static List<Bytes32> resolveBytes32FromAbiReturn(String abiReturn) {
        List<TypeReference<?>> outputParameters = Collections.singletonList(
                new TypeReference<DynamicArray<Bytes32>>() {
                });
        List<Type> output = FunctionReturnDecoder.decode(abiReturn, convert(outputParameters));
        return (List<Bytes32>) output.get(0).getValue();
    }

    private static List<EirIdentifier> getIdentifiers(List<Bytes32> ids) {
        List<EirIdentifier> identifiers = new ArrayList<>();
        for (Bytes32 identifier : ids) {
            identifiers.add(new EirIdentifier(new String(identifier.getValue(), StandardCharsets.UTF_8)));
        }
        return identifiers;
    }
}