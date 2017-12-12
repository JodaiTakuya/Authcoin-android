package com.authcoinandroid.module.challenges.signing;

import com.authcoinandroid.module.challenges.Challenge;

import java.security.SecureRandom;

public class SigningChallenge implements Challenge {

    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public String getType() {
        return "Sign Content";
    }

    @Override
    public byte[] getContent() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }

}
