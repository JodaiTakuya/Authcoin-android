package com.authcoinandroid.service.contract;

import com.authcoinandroid.model.contract.ContractRequest;
import com.authcoinandroid.model.contract.ContractResponse;
import com.authcoinandroid.service.qtum.BlockChainService;
import com.authcoinandroid.util.crypto.Keccak;
import com.authcoinandroid.util.crypto.Parameters;
import org.spongycastle.util.encoders.Hex;
import rx.Observable;

public class AuthcoinContractService {

    private static AuthcoinContractService authcoinContractService;
    private final BlockChainService blockChainService;

    private final static String CONTRACT_ADDRESS = "c740d9cf5a7ca6e13f2a34bf5a661180c74d2161";
    private final static String GET_EIR_COUNT = "getEirCount";

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
        return callContractMethod(new ContractRequest(getHash(GET_EIR_COUNT)));
    }

    private Observable<ContractResponse> callContractMethod(ContractRequest contractRequest) {
        return blockChainService.callSmartContract(CONTRACT_ADDRESS, contractRequest);
    }

    private String[] getHash(String methodName) {
        Keccak keccak = new Keccak();
        return new String[]{keccak.getHash(Hex.toHexString((methodName + "()").getBytes()), Parameters.KECCAK_256).substring(0, 8)};
    }
}