package com.authcoinandroid.model;

import com.authcoinandroid.util.crypto.CryptoUtil;
import io.requery.*;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.Hash;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.List;

import static org.web3j.utils.Numeric.cleanHexPrefix;

/**
 * This is requery class. Based on this class EntityIdentityRecord is generated.
 */
@Entity
public class BaseEntityIdentityRecord {

    @Key
    byte[] id;

    @OneToMany(cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    List<EirIdentifier> identifiers;

    @OneToMany
    List<ChallengeRecord> challenges;

    boolean revoked;

    String contentType;
    byte[] content;

    byte[] hash;
    byte[] signature;

    // Android key-store alias. Used to look up private and public key.
    String keyStoreAlias;
    // EIR status on blockchain
    AssetBlockChainStatus status;
    // Id of the transaction where eir was created
    String transactionId;

    @Transient
    KeyPair keyPair;
    @Transient
    PublicKey publicKey;

    /*
    * Creates a new entity identity record.
    */
    public BaseEntityIdentityRecord(String contentType, String keyStoreAlias, KeyPair keyPair) {
        this.revoked = false;
        this.contentType = contentType;
        this.keyStoreAlias = keyStoreAlias;
        this.status = AssetBlockChainStatus.SUBMITTED;
        this.keyPair = keyPair;
        this.publicKey = keyPair.getPublic();
        this.content = publicKey.getEncoded();
        this.id = calcId();
        this.hash = new byte[32]; // TODO calculate correct hash
        this.signature = new byte[128]; // TODO calculate correct signature
    }

    public BaseEntityIdentityRecord() {
    }

    @PostLoad
    public void init() {
        this.publicKey = CryptoUtil.toPublicKey(content);
        this.keyPair = CryptoUtil.getKeyPair(keyStoreAlias);
        this.hash = calculateHash();
        this.signature = sign();
    }

    public void setIdentifiers(List<EirIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    private byte[] calcId() {
        //TODO Hash.sha3(keyPair.getPublic().getEncoded())?
        String pubKeyAsHex = Hex.toHexString(getPublicKey().getEncoded());
        return Hex.decode(cleanHexPrefix(Hash.sha3(pubKeyAsHex)));
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public void setStatus(AssetBlockChainStatus status) {
        this.status = status;
    }

    public PublicKey getPublicKey() {
        return CryptoUtil.getKeyPair(keyStoreAlias).getPublic();
    }

    public byte[] getId() {
        return id;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public byte[] getHash() {
        return hash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public String getKeyStoreAlias() {
        return keyStoreAlias;
    }

    public AssetBlockChainStatus getStatus() {
        return status;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    // TODO implement
    byte[] calculateHash() {
        return new byte[32];
    }

    // TODO sign
    byte[] sign() {
        return new byte[128];
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}