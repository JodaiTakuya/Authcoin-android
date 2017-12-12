package com.authcoinandroid.service.challenge;

import android.support.annotation.NonNull;

import com.authcoinandroid.AbstractTest;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.module.challenges.Challenges;
import com.authcoinandroid.service.identity.EirRepository;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.util.Util;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class ChallengeRepositoryTest extends AbstractTest {

    private EirRepository eirRepository;
    private ChallengeRepository challengeRepository;
    private EntityIdentityRecord verifier;
    private EntityIdentityRecord target;
    private ReactiveEntityStore<Persistable> dataStore;

    @Before
    public void setUp() throws Exception {
        this.dataStore = ((AuthCoinApplication) RuntimeEnvironment.application).getDataStore();
        this.eirRepository = new EirRepository(dataStore);
        this.verifier = eirRepository.save(createEir("Verifier")).blockingGet();
        this.target = eirRepository.save(createEir("Target")).blockingGet();
        this.challengeRepository = new ChallengeRepository(dataStore);
    }

    @After
    public void tearDown() throws Exception {
        this.dataStore.close();
    }

    @Test
    public void testInsert() throws Exception {
        ChallengeRecord cr = createChallengeRecord();
        Single<ChallengeRecord> result = challengeRepository.save(cr);
        Assert.assertNotNull(result.blockingGet());

        ChallengeRecord dbResult = dataStore.findByKey(ChallengeRecord.class, cr.getId()).blockingGet();
        Assert.assertNotNull(dbResult);
        Assert.assertNotNull(dbResult.getVerifier());
        Assert.assertNotNull(dbResult.getTarget());
    }

    @Test
    public void testFindAll() throws Exception {
        challengeRepository.save(createChallengeRecord()).blockingGet();
        challengeRepository.save(createChallengeRecord()).blockingGet();

        List<ChallengeRecord> result = challengeRepository.findAll().toList();
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void testFindById() throws Exception {
        ChallengeRecord challenge = challengeRepository.save(createChallengeRecord()).blockingGet();
        ChallengeRecord cr = challengeRepository.find(challenge.getId()).blockingGet();

        Assert.assertNotNull(cr);
        Assert.assertTrue(Arrays.equals(challenge.getId(), cr.getId()));
    }

    @Test
    public void findByVaeId() throws Exception {
        ChallengeRecord challenge = challengeRepository.save(createChallengeRecord()).blockingGet();
        List<ChallengeRecord> records = challengeRepository.findByVaeId(challenge.getVaeId()).toList();

        Assert.assertNotNull(records);
        Assert.assertEquals(1, records.size());
        Assert.assertTrue(Arrays.equals(challenge.getId(), records.get(0).getId()));
    }

    @NonNull
    private ChallengeRecord createChallengeRecord() {
        byte[] id = Util.generateId();
        byte[] vaeId = Util.generateId();
        Challenge c = Challenges.get("Sign Content");
        return new ChallengeRecord(id, vaeId, c.getType(), c.getContent(), verifier, target);
    }
}