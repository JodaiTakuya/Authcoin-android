package com.authcoinandroid.service.contract;

import com.authcoinandroid.service.qtum.ContractRequest;
import com.authcoinandroid.service.qtum.UnspentOutput;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.script.Script;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;

import java.util.ArrayList;
import java.util.List;

import static com.authcoinandroid.service.contract.AuthcoinContractParams.*;
import static com.authcoinandroid.service.qtum.TransactionUtil.createScript;
import static com.authcoinandroid.service.qtum.TransactionUtil.createTransaction;
import static java.util.Collections.singletonList;
import static org.web3j.abi.FunctionEncoder.encode;
import static org.web3j.utils.Numeric.cleanHexPrefix;

class ContractMethodEncoder {

    static Script resolveScript(String methodName, List<Type> methodParameters) {
        String encodedFunction = encodeFunction(new Function(methodName, methodParameters, new ArrayList<>()));
        String abiMethod = encodedFunction.substring(0, 8);
        String abiParams = encodedFunction.substring(8);
        return createScript(encodedFunction, 250000, GAS_PRICE, CONTRACT_ADDRESS);
    }

    static String resolveTransaction(DeterministicKey key, Script script, List<UnspentOutput> unspentOutput) {
        return createTransaction(script, unspentOutput, singletonList(key), GAS_LIMIT, GAS_PRICE, FEE_PER_KB, FEE);
    }

    static ContractRequest resolveContractRequest(String methodName) {
        return new ContractRequest(new String[]{encodeFunction(new Function(methodName, new ArrayList<>(), new ArrayList<>()))});
    }

    static String encodeFunction(Function function) {
        return cleanHexPrefix(encode(function));
    }
}