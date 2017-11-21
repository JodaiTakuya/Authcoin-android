package com.authcoinandroid.model;

import java.security.Key;

public class EntityIdentityRecord {
    private String[] identifiers;
    private Key content;
    private String contentType;
    private byte[] hash;
    private byte[] signature;

    public String[] getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(String[] identifiers) {
        this.identifiers = identifiers;
    }

    public Key getContent() {
        return content;
    }

    public void setContent(Key content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}