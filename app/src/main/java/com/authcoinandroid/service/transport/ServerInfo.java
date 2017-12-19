package com.authcoinandroid.service.transport;

import java.util.UUID;


public class ServerInfo {
    private String serverEir;
    private int nonce;
    private UUID id;
    private String appName;

    public String getServerEir() {
        return serverEir;
    }

    public int getNonce() {
        return nonce;
    }

    public UUID getId() {
        return id;
    }

    public String getAppName() {
        return appName;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "serverEir='" + serverEir + '\'' +
                ", nonce=" + nonce +
                ", id=" + id +
                ", appName='" + appName + '\'' +
                '}';
    }
}
