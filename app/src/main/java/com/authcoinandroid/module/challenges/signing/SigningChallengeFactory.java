package com.authcoinandroid.module.challenges.signing;

import com.authcoinandroid.module.challenges.ChallengeFactory;
import com.authcoinandroid.module.challenges.Challenge;

public class SigningChallengeFactory implements ChallengeFactory {

    @Override
    public Challenge create() {
        return new SigningChallenge();
    }
}
