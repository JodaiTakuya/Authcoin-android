package com.authcoinandroid.service.identity;

public class EcEirBuilder extends EirBuilder {
    private EcEirBuilder() {
        super();
    }

    @Override
    EirBuilder setContentType() {
        entityIdentityRecord.setContentType("test");
//        entityIdentityRecord.setContentType("ec-pub-key");
        return this;
    }

    static EirBuilder newEcEirBuilder() {
        return new EcEirBuilder();
    }
}