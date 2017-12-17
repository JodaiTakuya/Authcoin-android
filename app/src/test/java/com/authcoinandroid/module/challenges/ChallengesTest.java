package com.authcoinandroid.module.challenges;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

@Ignore
public class ChallengesTest {

    private static final String CHALLENGE_NAME_DUMMY = "dummy";
    private static final String CHALLENGE_NAME_SIGN_CONTENT = "Sign Content";

    @Test
    public void testGetAllTypes() throws Exception {
        Set<String> types = Challenges.getAllTypes();
        assertEquals(1, types.size());
    }

    @Test
    public void testAddChallengeFactory() throws Exception {
        Challenges.add(CHALLENGE_NAME_DUMMY, new ChallengeFactory() {
            @Override
            public Challenge create() {
                return new Challenge() {
                    @Override
                    public String getType() {
                        return "dummy";
                    }

                    @Override
                    public byte[] getContent() {
                        return new byte[0];
                    }
                };
            }
        }, null);
        assertEquals(2, Challenges.getAllTypes().size());
    }

    @Test
    public void testGetSigningChallenge() throws Exception {
        Challenge challenge = Challenges.get(CHALLENGE_NAME_SIGN_CONTENT);
        assertNotNull(challenge);
        assertNotNull(challenge.getContent());
        assertEquals("Sign Content", challenge.getType());
        assertTrue(challenge.getContent().length > 0);
    }
}