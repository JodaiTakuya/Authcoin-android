package com.authcoinandroid.module.challenges;

import com.authcoinandroid.module.challenges.signing.SigningChallengeFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Singleton that holds {@link ChallengeFactory} instances.
 * NB! This class isn't thread safe.
 */
public final class Challenges {

    private static Map<String, ChallengeFactory> factories = new HashMap<>();

    static {
        factories.put("Sign Content", new SigningChallengeFactory());
        // TODO add more challenges :)
    }

    private Challenges() {
        // Singleton
    }

    public static Set<String> getAllTypes() {
        return factories.keySet();
    }

    public static void add(String type, ChallengeFactory factory) {
        if (factories.containsKey(type)) {
            throw new IllegalArgumentException("Challenge Factory already registered");
        }
        factories.put(type, factory);
    }

    public static Challenge get(String type) {
        ChallengeFactory factory = factories.get(type);
        if (factory == null) {
            throw new NullPointerException("Unknown challenge type");
        }
        return factory.create();
    }
    
}
