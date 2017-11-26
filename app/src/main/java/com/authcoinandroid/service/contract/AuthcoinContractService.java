package com.authcoinandroid.service.contract;

import com.authcoinandroid.service.qtum.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.QtumTestNetParams;
import org.bitcoinj.script.Script;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import rx.Observable;

import java.util.List;

import static com.authcoinandroid.service.contract.AuthcoinContractParams.AUTHCOIN_CONTRACT_ADDRESS;
import static com.authcoinandroid.service.contract.ContractMethodEncoder.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class AuthcoinContractService {
    private final static String LOG_TAG = "AuthcoinContractService";
    private static AuthcoinContractService authcoinContractService;
    private final BlockChainService blockChainService;

    private final static String GET_EIR_COUNT = "getEirCount";
    private final static String GET_EIR = "getEir";
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

    public Observable<SendRawTransactionResponse> registerEir(final DeterministicKey key, List<Type> methodParameters) {
        return sendRawTransaction(key, resolveScript(REGISTER_EIR, methodParameters));
    }

    public Observable<ContractResponse> getEirCount() {
        return callContract(resolveContractRequest(GET_EIR_COUNT, emptyList()));
    }

    public Observable<ContractResponse> getEir(Bytes32 eirId) {
        return callContract(resolveContractRequest(GET_EIR, singletonList(eirId)));
    }

    public Observable<List<UnspentOutput>> getUnspentOutputs(DeterministicKey key) {
        return blockChainService.getUnspentOutput(singletonList(key.toAddress(QtumTestNetParams.get()).toBase58()));
    }

    private Observable<SendRawTransactionResponse> sendRawTransaction(DeterministicKey key, Script script) {
        return getUnspentOutputs(key).switchMap(
                unspentOutput -> blockChainService.sendRawTransaction(
                        new SendRawTransactionRequest(resolveTransaction(key, script, unspentOutput), 1)
                ));
    }

    private Observable<ContractResponse> callContract(ContractRequest contractRequest) {
        return blockChainService.callContract(AUTHCOIN_CONTRACT_ADDRESS, contractRequest);
    }
}