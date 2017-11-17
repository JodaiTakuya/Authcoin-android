package com.authcoinandroid.service.contract;

import android.util.Log;

import com.authcoinandroid.service.qtum.BlockChainService;
import com.authcoinandroid.service.qtum.ContractRequest;
import com.authcoinandroid.service.qtum.ContractResponse;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

import static org.web3j.abi.FunctionEncoder.encode;
import static org.web3j.utils.Numeric.cleanHexPrefix;

public class AuthcoinContractService {
    private final static String LOG_TAG = "AuthcoinContractService";
    private static AuthcoinContractService authcoinContractService;
    private final BlockChainService blockChainService;

    private final static String CONTRACT_ADDRESS = "c740d9cf5a7ca6e13f2a34bf5a661180c74d2161";
    private final static String GET_EIR_COUNT = "getEirCount";
    private final static String REGISTER_EIR = "registerEir";

    public static AuthcoinContractService getInstance() {
        if (authcoinContractService == null) {
            authcoinContractService = new AuthcoinContractService();
        }
        return authcoinContractService;
    }

    private AuthcoinContractService() {
        this.blockChainService = BlockChainService.getInstance();
    }

    public Observable<ContractResponse> registerEir(List<Type> contractMethodParameters) {
        //TODO this doesn't work. use sendRawTransaction method.
        String encode = encodeFunction(new Function(REGISTER_EIR, contractMethodParameters, new ArrayList<TypeReference<?>>()));
        Log.d(LOG_TAG, "Encoded method call for register eir: " + encode);
        return callContractMethod(new ContractRequest(new String[]{encode}));
    }

    public Observable<ContractResponse> getEirCount() {
        String encode = encodeFunction(new Function(GET_EIR_COUNT, new ArrayList<Type>(), new ArrayList<TypeReference<?>>()));
        Log.d(LOG_TAG, "Encoded method call: " + encode);
        return callContractMethod(new ContractRequest(new String[]{encode}));
    }

    private String encodeFunction(Function function) {
        return cleanHexPrefix(encode(function));
    }

    private Observable<ContractResponse> callContractMethod(ContractRequest contractRequest) {
        return blockChainService.callContract(CONTRACT_ADDRESS, contractRequest);
    }
}