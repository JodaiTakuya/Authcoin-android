package com.authcoinandroid.module.v2;


import com.authcoinandroid.module.messaging.MessageHandler;

public abstract class AbstractModule {

    protected MessageHandler messageHandler;

    public AbstractModule(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

}
