package com.authcoinandroid;

import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.JdkKeyPairService;
import com.authcoinandroid.service.keypair.KeyPairService;
import com.authcoinandroid.ui.AuthCoinApplication;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static android.os.Build.VERSION_CODES.M;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {M}, application = AuthCoinApplication.class)
public abstract class AbstractTest {

    protected KeyPairService keyPairService = new JdkKeyPairService();

    protected EntityIdentityRecord createEir(String alias) {
        return new EntityIdentityRecord("test", alias, keyPairService.create(alias));
    }

}
