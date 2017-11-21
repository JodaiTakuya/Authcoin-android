package com.authcoinandroid.service.identity;

import android.text.TextUtils;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.util.crypto.CryptoUtil;

import java.security.*;

import static com.authcoinandroid.util.crypto.CryptoUtil.hashSha256;

abstract class EirBuilder {

    EntityIdentityRecord entityIdentityRecord;

    EirBuilder() {
        this.entityIdentityRecord = new EntityIdentityRecord();
    }

    EirBuilder addIdentifiers(String[] identifiers) {
        entityIdentityRecord.setIdentifiers(identifiers);
        return this;
    }

    EntityIdentityRecord getEir() {
        return this.entityIdentityRecord;
    }

    EirBuilder calculateHash() throws NoSuchAlgorithmException {
        String data = TextUtils.join(",", entityIdentityRecord.getIdentifiers())
                + new String(entityIdentityRecord.getContent().getEncoded())
                + entityIdentityRecord.getContentType();
        entityIdentityRecord.setHash(hashSha256(data));
        return this;
    }

    EirBuilder signHash(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        entityIdentityRecord.setSignature(CryptoUtil.sign(privateKey, entityIdentityRecord.getHash()));
        return this;
    }

    EirBuilder addContent(PublicKey key) {
        entityIdentityRecord.setContent(key);
        return this;
    }

    abstract EirBuilder setContentType();
}