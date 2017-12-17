package com.authcoinandroid.module.challenges.signing;

import com.authcoinandroid.AbstractTest;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.module.challenges.Challenges;

import org.junit.Ignore;
import org.junit.Test;

import java.security.Signature;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@Ignore
public class SigningChallengeExecutorTest extends AbstractTest {

    private SigningChallengeExecutor executor = new SigningChallengeExecutor();

    @Test
    public void testExecute() throws Exception {
        EntityIdentityRecord eir = createEir("Test");
        Challenge challenge = Challenges.get("Sign Content");
        byte[] result = executor.execute(challenge.getContent(), eir);
        assertNotNull(result);

        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initVerify(eir.getKeyPair().getPublic());
        signature.update(challenge.getContent());
        assertTrue(signature.verify(result));
    }

}
