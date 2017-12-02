package com.authcoinandroid.service.identity;

import android.text.TextUtils;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.util.crypto.CryptoUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import static com.authcoinandroid.util.crypto.CryptoUtil.hashSha256;

public abstract class EirBuilder {

    EntityIdentityRecord entityIdentityRecord;

    EirBuilder() {
        this.entityIdentityRecord = new EntityIdentityRecord();
    }

    public EirBuilder addIdentifiers(String[] identifiers) {
        entityIdentityRecord.setIdentifiers(identifiers);
        return this;
    }

    public EntityIdentityRecord getEir() {
        return this.entityIdentityRecord;
    }

    public EirBuilder calculateHash() throws NoSuchAlgorithmException {
        String data = TextUtils.join(",", entityIdentityRecord.getIdentifiers())
                + new String(entityIdentityRecord.getContent().getEncoded())
                + entityIdentityRecord.getContentType();
        entityIdentityRecord.setHash(hashSha256(data));
        return this;
    }

    public EirBuilder signHash(KeyPair keyPair) throws GeneralSecurityException, IOException {
        entityIdentityRecord.setSignature(CryptoUtil.sign(entityIdentityRecord.getHash(), keyPair));
        return this;
    }

    public EirBuilder addContent(PublicKey key) {
        entityIdentityRecord.setContent(key);
        return this;
    }

    public abstract EirBuilder setContentType();
}