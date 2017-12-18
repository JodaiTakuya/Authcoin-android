package com.authcoinandroid.module.challenges;

import android.util.Pair;

import com.authcoinandroid.module.challenges.signing.SigningChallengeExecutor;
import com.authcoinandroid.module.challenges.signing.SigningChallengeFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Singleton that holds {@link ChallengeFactory} instances.
 * NB! This class isn't thread safe.
 */
public final class Challenges {

    private static Map<String, Pair<ChallengeFactory, ChallengeExecutor>> factories = new HashMap<>();


    static {
        factories.put(String.valueOf(ChallengeType.SIGN_CONTENT),
                Pair.create(new SigningChallengeFactory(), new SigningChallengeExecutor()));
    }

    private Challenges() {
        // Singleton
    }

    public static Set<String> getAllTypes() {
        return factories.keySet();
    }

    public static void add(String type, ChallengeFactory factory, ChallengeExecutor executor) {
        if (factories.containsKey(type)) {
            throw new IllegalArgumentException("Challenge Factory already registered");
        }
        factories.put(type, Pair.create(factory, executor));
    }

    public static Challenge get(String type) {
        ChallengeFactory factory = factories.get(type).first;
        if (factory == null) {
            throw new NullPointerException("Unknown challenge type");
        }
        return factory.create();
    }

    public static ChallengeExecutor getExecutor(String type) {
        ChallengeExecutor executor = factories.get(type).second;
        if (executor == null) {
            throw new NullPointerException("Unknown challenge type");
        }
        return executor;
    }

}
