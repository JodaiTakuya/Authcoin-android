package com.authcoinandroid.util.crypto;

import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class CryptoUtilTest {

    private KeyPair keyPair;

    @Before
    public void setUp() throws Exception {
        keyPair = CryptoUtil.createEcKeyPair();
    }

    @Test
    public void createEcKeyPair() throws Exception {
        assertThat(keyPair.getPublic().getAlgorithm(), is("EC"));
        assertThat(keyPair.getPrivate().getAlgorithm(), is("EC"));
    }

    @Test
    public void sign() throws Exception {
        String data = "thisIsTest";
        byte[] sign = CryptoUtil.sign(keyPair.getPrivate(), data.getBytes());
        assertThat(new String(sign), is(not(data)));
    }

    @Test
    public void verify() throws Exception {
        String data = "thisIsTest";
        byte[] sign = CryptoUtil.sign(keyPair.getPrivate(), data.getBytes());
        assertThat(CryptoUtil.verify(keyPair.getPublic(), sign, data.getBytes()), is(true));
    }

    @Test
    public void hashSha256() throws Exception {
        String data = "thisIsTest";
        byte[] bytes = CryptoUtil.hashSha256(data);
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        assertThat(sb.toString(), is("138de040f5932091f0b8a850078e4685c4af26794301bf0d17ad7ac571ee4f5e"));
    }
}