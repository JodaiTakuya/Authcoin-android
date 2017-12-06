package com.authcoinandroid.module;

import android.util.Pair;

import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.identity.EirRepository;
import com.authcoinandroid.ui.AuthCoinApplication;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.security.KeyPair;
import java.security.Security;

import static android.os.Build.VERSION_CODES.M;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {M}, application = AuthCoinApplication.class)
public class KeyGenerationAndEstablishBindingModuleTest {
    @Test
    public void testGenerateAndEstablishBinding() throws Exception {
        EirRepository repository = EirRepository.getInstance(RuntimeEnvironment.application);
        JdkKeyPairService keyPairService = new JdkKeyPairService();
        KeyGenerationAndEstablishBindingModule module = new KeyGenerationAndEstablishBindingModule(repository, keyPairService);
        Pair<KeyPair, EntityIdentityRecord> pair = module.generateAndEstablishBinding(new String[]{"test"}, "Test");

        Assert.assertNotNull(pair);
        Assert.assertEquals(keyPairService.get("Test"), pair.first);
        Assert.assertNotNull(pair.second);
        Assert.assertNotNull(pair.second.getIdentifiers());
        Assert.assertEquals(1, pair.second.getIdentifiers().size());
        Assert.assertEquals("test", pair.second.getIdentifiers().get(0).getValue());
    }

}