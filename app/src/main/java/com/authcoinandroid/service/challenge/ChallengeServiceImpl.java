package com.authcoinandroid.service.challenge;

import com.authcoinandroid.model.ChallengeRecord;


public class ChallengeServiceImpl implements ChallengeService {

    private ChallengeRepository challengeRepository;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public void registerChallenge(ChallengeRecord challenge) {
        this.challengeRepository.save(challenge);
        // TODO send data to BC
    }

    @Override
    public boolean isProcessed(byte[] vaeId) {
        // TODO query DB
        return false;
    }


}
