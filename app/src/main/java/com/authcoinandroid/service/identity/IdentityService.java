package com.authcoinandroid.service.identity;

import android.content.Context;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.contract.AuthcoinContractService;
import com.authcoinandroid.service.qtum.mapper.RecordContractParamMapper;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.web3j.abi.datatypes.Type;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
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

    public void registerEirWithEcKey(Context context, String[] identifiers) throws UnreadableWalletException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        KeyPair keyPair = createEcKeyPair();
        EntityIdentityRecord eir = newEcEirBuilder()
                .addIdentifiers(identifiers)
                .addContent(keyPair.getPublic())
                .setContentType()
                .calculateHash()
                .signHash(keyPair.getPrivate())
                .getEir();
        List<Type> params = RecordContractParamMapper.resolveEirContractParams(eir);
        AuthcoinContractService.getInstance().registerEir(WalletService.getInstance().getReceiveKey(context), params);
    }
}