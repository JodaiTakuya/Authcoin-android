package com.authcoinandroid.service.identity;

import android.content.Context;
import com.authcoinandroid.exception.RegisterEirException;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.contract.AuthcoinContractService;
import com.authcoinandroid.service.qtum.SendRawTransactionResponse;
import com.authcoinandroid.service.qtum.mapper.RecordContractParamMapper;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.web3j.abi.datatypes.Type;
import rx.Observable;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;

import static com.authcoinandroid.service.identity.EcEirBuilder.newEcEirBuilder;
import static com.authcoinandroid.util.crypto.CryptoUtil.createEcKeyPair;

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

    public Observable<SendRawTransactionResponse> registerEirWithEcKey(Context context, String[] identifiers, String alias) throws RegisterEirException {
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
            return AuthcoinContractService.getInstance().registerEir(WalletService.getInstance().getReceiveKey(context), params);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                NoSuchProviderException | IOException | SignatureException | CertificateException |
                InvalidKeyException | UnrecoverableEntryException | KeyStoreException | UnreadableWalletException e) {
            throw new RegisterEirException("Failed to register eir", e);
        }
    }
}