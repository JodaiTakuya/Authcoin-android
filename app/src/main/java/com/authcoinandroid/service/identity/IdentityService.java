package com.authcoinandroid.service.identity;

import android.app.Application;

import com.authcoinandroid.exception.GetAliasException;
import com.authcoinandroid.exception.GetEirException;
import com.authcoinandroid.exception.RegisterEirException;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.KeyGenerationAndEstablishBindingModule;
import com.authcoinandroid.service.contract.AuthcoinContractService;
import com.authcoinandroid.service.qtum.SendRawTransactionResponse;
import com.authcoinandroid.service.qtum.mapper.RecordContractParamMapper;
import com.authcoinandroid.util.ContractUtil;
import com.authcoinandroid.util.crypto.CryptoUtil;

import org.bitcoinj.crypto.DeterministicKey;
import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;

import io.reactivex.Observable;

import static com.authcoinandroid.util.ContractUtil.bytesToBytes32;
import static com.authcoinandroid.util.crypto.CryptoUtil.getPublicKeyByAlias;

public class IdentityService {

    private static IdentityService identityService;
    private final EirRepository repository;

    public static IdentityService getInstance(Application application) {
        if (identityService == null) {
            identityService = new IdentityService(EirRepository.getInstance(application));
        }
        return identityService;
    }

    private IdentityService(EirRepository repository) {
        this.repository = repository;
    }

    /**
     * Registers a new eir. Does the following:
     *  1. Generates a new KeyPair and saves it to Android KeyStore
     *  2. Creates EIR object and saves it to the local database
     *  3. Calls QTUM smart contract to store the EIR on blockchain
     *
     * @param key - wallet key
     * @param identifiers - EIR identifiers
     * @param alias - alias used by Android Keystore
     * @return an observable
     * @throws RegisterEirException will be thrown if registration fails
     */
    public Observable<SendRawTransactionResponse> registerEir(DeterministicKey key, String[] identifiers, String alias) throws RegisterEirException {
        try {
            KeyGenerationAndEstablishBindingModule module = new KeyGenerationAndEstablishBindingModule(repository);
            EntityIdentityRecord eir = module.generateAndEstablishBinding(identifiers, alias).second;
            List<Type> params = RecordContractParamMapper.resolveEirContractParams(eir);
            return AuthcoinContractService.getInstance().registerEir(key, params);
        } catch (GeneralSecurityException | IOException e) {
            throw new RegisterEirException("Failed to register EIR", e);
        }
    }

    public Observable<EntityIdentityRecord> getEir(String alias) throws GetEirException {
        try {
            PublicKey key = getPublicKeyByAlias(alias);
            return AuthcoinContractService.getInstance().getEir(getEirIdAsBytes32(key))
                    .switchMap(contractResponse -> mapAbiResponseToObservable(key, contractResponse.getItems().get(0).getOutput()));
        } catch (GeneralSecurityException | IOException e) {
            throw new GetEirException("Failed to get EIR", e);
        }
    }

    /**
     * Returns all entity identity records.
     */
    public List<EntityIdentityRecord> getAll() {
        return repository.findAll();
    }

    private Observable<EntityIdentityRecord> mapAbiResponseToObservable(PublicKey key, String abiResponse) {
        return Observable.defer(() -> {
            try {

                EntityIdentityRecord eir = RecordContractParamMapper.resolveEirFromAbiReturn(abiResponse);
                // TODO compare keys
                //eir.setContent(key);
                return Observable.just(eir);
            } catch (Exception e) {
                return Observable.error(e);
            }
        });
    }

    private Bytes32 getEirIdAsBytes32(PublicKey key) {
        return bytesToBytes32(Hex.decode(ContractUtil.getEirIdAsString(key)));
    }
}