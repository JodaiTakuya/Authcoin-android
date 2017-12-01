package com.authcoinandroid.service.identity;

public class EcEirBuilder extends EirBuilder {
    private EcEirBuilder() {
        super();
    }

    @Override
    public EirBuilder setContentType() {
        entityIdentityRecord.setContentType("test");
//        entityIdentityRecord.setContentType("ec-pub-key");
        return this;
    }

    public static EirBuilder newEcEirBuilder() {
        return new EcEirBuilder();
    }
}