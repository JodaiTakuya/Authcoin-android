package com.authcoinandroid.service.contract;

import android.util.Log;
import com.authcoinandroid.service.qtum.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.QtumTestNetParams;
import org.bitcoinj.script.Script;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import rx.Observable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.authcoinandroid.service.qtum.TransactionUtil.createTransaction;
import static java.util.Collections.singletonList;
import static org.web3j.abi.FunctionEncoder.encode;
import static org.web3j.utils.Numeric.cleanHexPrefix;

public class AuthcoinContractService {
    private final static String LOG_TAG = "AuthcoinContractService";
    private static AuthcoinContractService authcoinContractService;
    private final BlockChainService blockChainService;

    private final static String CONTRACT_ADDRESS = "c740d9cf5a7ca6e13f2a34bf5a661180c74d2161";
    private final static String GET_EIR_COUNT = "getEirCount";
    private final static String REGISTER_EIR = "registerEir";

    private static final int GAS_LIMIT = 25000;
    private static final int GAS_PRICE = 40;
    private static final BigDecimal FEE_PER_KB = BigDecimal.valueOf(0.004);
    private static final BigDecimal FEE = BigDecimal.valueOf(0.1);

    public static AuthcoinContractService getInstance() {
        if (authcoinContractService == null) {
            authcoinContractService = new AuthcoinContractService();
        }
        return authcoinContractService;
    }

    private AuthcoinContractService() {
        this.blockChainService = BlockChainService.getInstance();
    }

    public Observable<SendRawTransactionResponse> registerEir(final DeterministicKey key, List<Type> contractMethodParameters) {
        final Script script = resolveScript(REGISTER_EIR, contractMethodParameters);
        return getUnspentOutputs(key).switchMap(
                unspentOutput -> blockChainService.sendRawTransaction(
                        new SendRawTransactionRequest(createTransaction(script, unspentOutput, singletonList(key), GAS_LIMIT, GAS_PRICE, FEE_PER_KB, FEE), 1)
                ));
    }

    public Observable<ContractResponse> getEirCount() {
        String encode = encodeFunction(new Function(GET_EIR_COUNT, new ArrayList<>(), new ArrayList<>()));
        Log.d(LOG_TAG, "Encoded method call: " + encode);
        return callContractMethod(new ContractRequest(new String[]{encode}));
    }

    private Observable<List<UnspentOutput>> getUnspentOutputs(DeterministicKey key) {
        return blockChainService.getUnspentOutput(singletonList(key.toAddress(QtumTestNetParams.get()).toBase58()));
    }

    private Script resolveScript(String methodName, List<Type> contractMethodParameters) {
        // TODO this should probably be in its own class
        String encodedFunction = encodeFunction(new Function(methodName, contractMethodParameters, new ArrayList<>()));
        String abiMethod = encodedFunction.substring(0, 8);
        // TODO abiParams seems to be wrong
        String abiParams = encodedFunction.substring(8);
        Log.d(LOG_TAG, "Encoded method: " + abiMethod);
        Log.d(LOG_TAG, "Encoded params: " + abiParams);
        return TransactionUtil.createScript(abiMethod, abiParams, 250000, GAS_PRICE, CONTRACT_ADDRESS);
    }

    private String encodeFunction(Function function) {
        return cleanHexPrefix(encode(function));
    }

    private Observable<ContractResponse> callContractMethod(ContractRequest contractRequest) {
        return blockChainService.callContract(CONTRACT_ADDRESS, contractRequest);
    }
}