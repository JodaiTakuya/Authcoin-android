package com.authcoinandroid.service.contract;

import com.authcoinandroid.service.qtum.ContractRequest;
import com.authcoinandroid.service.qtum.UnspentOutput;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.script.Script;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;

import java.util.List;

import static com.authcoinandroid.service.contract.AuthcoinContractParams.*;
import static com.authcoinandroid.service.qtum.TransactionUtil.createScript;
import static com.authcoinandroid.service.qtum.TransactionUtil.createTransaction;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.web3j.abi.FunctionEncoder.encode;
import static org.web3j.utils.Numeric.cleanHexPrefix;

public class ContractMethodEncoder {

    static Script resolveScript(String methodName, List<Type> methodParameters) {
        String encodedFunction = encodeFunction(new Function(methodName, methodParameters, emptyList()));
        return createScript(encodedFunction, FUNCTION_GAS_LIMIT, GAS_PRICE, AUTHCOIN_CONTRACT_ADDRESS);
    }

    public static String encodeParameter(Type type) {
        return encodeFunction(new Function("", singletonList(type), emptyList())).substring(8);
    }

    static String resolveTransaction(DeterministicKey key, Script script, List<UnspentOutput> unspentOutput) {
        return createTransaction(script, unspentOutput, singletonList(key), GAS_LIMIT, GAS_PRICE, FEE_PER_KB, FEE);
    }

    static ContractRequest resolveContractRequest(String methodName, List<Type> methodParameters) {
        return new ContractRequest(new String[]{encodeFunction(new Function(methodName, methodParameters, emptyList()))});
    }

    static String encodeFunction(Function function) {
        return cleanHexPrefix(encode(function));
    }
}