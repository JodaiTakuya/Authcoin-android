package com.authcoinandroid.service.contract;

import android.util.Log;
import com.authcoinandroid.model.contract.ContractMethodParameter;
import com.authcoinandroid.model.contract.ContractRequest;
import com.authcoinandroid.model.contract.ContractResponse;
import com.authcoinandroid.service.qtum.BlockChainService;
import rx.Observable;

import java.util.List;

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

    public Observable<ContractResponse> getEirCount() {
        String encodedMethodCall = new ContractMethodEncoder().encodeMethodCall(GET_EIR_COUNT);
        Log.d(LOG_TAG, "Encoded method call: " + encodedMethodCall);
        return callContractMethod(new ContractRequest(new String[]{encodedMethodCall}));
    }

    public Observable<ContractResponse> registerEir(List<ContractMethodParameter> params) {
        String encodedMethodCall = new ContractMethodEncoder().encodeParamsForMethod(REGISTER_EIR, params);
        Log.d(LOG_TAG, "Encoded method call: " + encodedMethodCall);
        return callContractMethod(new ContractRequest(new String[]{encodedMethodCall}));
    }

    private Observable<ContractResponse> callContractMethod(ContractRequest contractRequest) {
        return blockChainService.callSmartContract(CONTRACT_ADDRESS, contractRequest);
    }
}