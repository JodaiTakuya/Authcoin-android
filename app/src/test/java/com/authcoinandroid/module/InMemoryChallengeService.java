package com.authcoinandroid.module;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.service.challenge.ChallengeService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryChallengeService implements ChallengeService {

    private Map<byte[], List<ChallengeRecord>> vaeIdToChallenges = new HashMap<>();

    @Override
    public void registerChallenge(ChallengeRecord challenge) {
        List<ChallengeRecord> challenges = vaeIdToChallenges.get(challenge.getVaeId());
        if (challenges == null) {
            challenges = new LinkedList<>();
        }
        challenges.add(challenge);
        vaeIdToChallenges.put(challenge.getVaeId(), challenges);
    }

    @Override
    public boolean isProcessed(byte[] vaeId) {
        return vaeIdToChallenges.get(vaeId).size() > 1;
    }
}
