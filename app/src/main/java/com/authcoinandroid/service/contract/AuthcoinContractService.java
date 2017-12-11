package com.authcoinandroid.service.contract;

import com.authcoinandroid.exception.GetEirException;
import com.authcoinandroid.service.qtum.*;
import io.reactivex.Observable;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.QtumTestNetParams;
import org.bitcoinj.script.Script;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;

import java.util.List;

import static android.text.TextUtils.isEmpty;
import static com.authcoinandroid.service.contract.AuthcoinContractParams.AUTHCOIN_CONTRACT_ADDRESS;
import static com.authcoinandroid.service.contract.ContractMethodEncoder.*;
import static com.authcoinandroid.util.ContractUtil.stripLeadingZeroes;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.web3j.utils.Numeric.cleanHexPrefix;

public class AuthcoinContractService {
    private final static String LOG_TAG = "AuthcoinContractService";
    private static final String GET_CHALLENGE_IDS = "getChallengeIds";
    private static final String GET_CHALLENGE_ADDRESS = "getChallenge";
    private static AuthcoinContractService authcoinContractService;
    private final BlockChainService blockChainService;

    private final static String GET_EIR_COUNT = "getEirCount";
    private final static String REGISTER_EIR = "registerEir";
    private final static String GET_EIR = "getEir";
    private final static String GET_EIR_DATA = "getData";
    // TODO change GET_CR_DATA to getData which returns all, currently for testing returns only type
    private final static String GET_CR_DATA = "getChallengeType";
    private final static String GET_VAE_ARRAY_BY_EIR = "getVaeArrayByEirId";

    public static AuthcoinContractService getInstance() {
        if (authcoinContractService == null) {
            authcoinContractService = new AuthcoinContractService();
        }
        return authcoinContractService;
    }

    public AuthcoinContractService() {
        this.blockChainService = BlockChainService.getInstance();
    }

    public Observable<SendRawTransactionResponse> registerEir(final DeterministicKey key, List<Type> methodParameters) {
        return sendRawTransaction(key, resolveAuthCoinScript(REGISTER_EIR, methodParameters));
    }

    public Observable<ContractResponse> getEirCount() {
        return callAuthCoinContract(resolveContractRequest(GET_EIR_COUNT, emptyList()));
    }

    public Observable<ContractResponse> getEirAddress(Bytes32 eirId) {
        return callAuthCoinContract(resolveContractRequest(GET_EIR, singletonList(eirId)));
    }

    public Observable<ContractResponse> getEirByAddress(String address) {
        String eirAddress = stripLeadingZeroes(address);
        if (isEmpty(eirAddress)) {
            return Observable.error(new GetEirException("No such EIR with provided eirId"));
        } else {
            return callContract(eirAddress, resolveContractRequest(GET_EIR_DATA, emptyList()));
        }
    }

    public Observable<List<UnspentOutput>> getUnspentOutputs(DeterministicKey key) {
        return blockChainService.getUnspentOutput(singletonList(key.toAddress(QtumTestNetParams.get()).toBase58()));
    }

    public Observable<History> getTransaction(String transaction) {
        return blockChainService.getTransaction(transaction);
    }

    public Observable<ContractResponse> getVaeArrayByEirId(Bytes32 eirId) {
        return callAuthCoinContract(resolveContractRequest(GET_VAE_ARRAY_BY_EIR, singletonList(eirId)));
    }

    public Observable<ContractResponse> getChallengeIds(Address address) {
        return callContract(cleanHexPrefix(address.toString()), resolveContractRequest(GET_CHALLENGE_IDS, emptyList()));
    }

    public Observable<ContractResponse> getChallengeAddress(Address address, Bytes32 challengeId) {
        return callContract(cleanHexPrefix(address.toString()), resolveContractRequest(GET_CHALLENGE_ADDRESS, singletonList(challengeId)));
    }

    public Observable<ContractResponse> getChallengeRecord(String challengeRecordAddress) {
        return callContract(stripLeadingZeroes(challengeRecordAddress), resolveContractRequest(GET_CR_DATA, emptyList()));
    }

    private Observable<SendRawTransactionResponse> sendRawTransaction(DeterministicKey key, Script script) {
        return getUnspentOutputs(key).switchMap(
                unspentOutput -> blockChainService.sendRawTransaction(
                        new SendRawTransactionRequest(resolveTransaction(key, script, unspentOutput), 1)
                ));
    }

    private Observable<ContractResponse> callAuthCoinContract(ContractRequest contractRequest) {
        return callContract(AUTHCOIN_CONTRACT_ADDRESS, contractRequest);
    }

    private Observable<ContractResponse> callContract(String contractAddress, ContractRequest contractRequest) {
        return blockChainService.callContract(contractAddress, contractRequest);
    }
}