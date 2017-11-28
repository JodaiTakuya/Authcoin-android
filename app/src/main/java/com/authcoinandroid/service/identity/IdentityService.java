package com.authcoinandroid.service.identity;

import com.authcoinandroid.exception.GetAliasException;
import com.authcoinandroid.exception.GetEirException;
import com.authcoinandroid.exception.RegisterEirException;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.contract.AuthcoinContractService;
import com.authcoinandroid.service.qtum.SendRawTransactionResponse;
import com.authcoinandroid.service.qtum.mapper.RecordContractParamMapper;
import com.authcoinandroid.util.crypto.CryptoUtil;
import org.bitcoinj.crypto.DeterministicKey;
import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Hash;
import rx.Observable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.List;

import static com.authcoinandroid.service.identity.EcEirBuilder.newEcEirBuilder;
import static com.authcoinandroid.util.ContractUtil.bytesToBytes32;
import static com.authcoinandroid.util.crypto.CryptoUtil.createEcKeyPair;
import static com.authcoinandroid.util.crypto.CryptoUtil.getPublicKeyByAlias;
import static org.web3j.utils.Numeric.cleanHexPrefix;

public class IdentityService {

    private static IdentityService identityService;

    public static IdentityService getInstance() {
        if (identityService == null) {
            identityService = new IdentityService();
        }
        return identityService;
    }

    private IdentityService() {
    }

    public Observable<SendRawTransactionResponse> registerEirWithEcKey(DeterministicKey key, String[] identifiers, String alias) throws RegisterEirException {
        try {
            KeyPair keyPair = createEcKeyPair(alias);
            EntityIdentityRecord eir = newEcEirBuilder()
                    .addIdentifiers(identifiers)
                    .addContent(keyPair.getPublic())
                    .setContentType()
                    .calculateHash()
                    .signHash(alias)
                    .getEir();
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

    public List<String> getAllAliases() throws GetAliasException {
        try {
            return CryptoUtil.getAliases();
        } catch (GeneralSecurityException | IOException e) {
            throw new GetAliasException("Can not get aliases", e);
        }
    }

    private Observable<EntityIdentityRecord> mapAbiResponseToObservable(PublicKey key, String abiResponse) {
        return Observable.defer(() -> {
            try {
                EntityIdentityRecord eir = RecordContractParamMapper.resolveEirFromAbiReturn(abiResponse);
                eir.setContent(key);
                return Observable.just(eir);
            } catch (Exception e) {
                return Observable.error(e);
            }
        });
    }

    private Bytes32 getEirIdAsBytes32(PublicKey key) {
        String pubKeyAsHex = Hex.toHexString(key.getEncoded());
        return bytesToBytes32(Hex.decode(cleanHexPrefix(Hash.sha3(pubKeyAsHex))));
    }
}