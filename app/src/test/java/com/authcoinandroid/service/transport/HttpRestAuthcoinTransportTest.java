package com.authcoinandroid.service.transport;

import com.authcoinandroid.AbstractTest;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.JdkKeyPairService;
import com.authcoinandroid.service.keypair.KeyPairService;
import com.authcoinandroid.util.Util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

@Ignore
public class HttpRestAuthcoinTransportTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void sendChallengeRecord() throws Exception {
        HttpRestAuthcoinTransport t =
                new HttpRestAuthcoinTransport("http://localhost:8080", null);

        ServerInfo serverInfo = t.start();

        EntityIdentityRecord verifier = createEir("Verifier");
        EntityIdentityRecord target = createEir("Target");
        ChallengeRecord record = new ChallengeRecord(
                Util.generateId(),
                Util.generateId(),
                "",
                new byte[32],
                verifier,
                target
        );
        ChallengeRecord result = t.send(serverInfo.getId(), record);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getId());
        Assert.assertArrayEquals(record.getVaeId(), result.getVaeId());
        Assert.assertArrayEquals(target.getId(), result.getVerifier().getId());
        Assert.assertArrayEquals(verifier.getId(), result.getTarget().getId());
    }

}