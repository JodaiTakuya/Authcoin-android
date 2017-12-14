package com.authcoinandroid.module;

import com.authcoinandroid.AbstractTest;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.challenge.ChallengeService;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ValidationAndAuthenticationProcessingModuleTest extends AbstractTest {

    public static final String CHALLENGE_TYPE = "Sign Content";
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private ValidationAndAuthenticationProcessingModule module;
    private ChallengeService challengeService;
    private EntityIdentityRecord verifier;
    private EntityIdentityRecord target;

    @Before
    public void setUp() throws Exception {
        FormalValidationModule fvm = new EcKeyFormalValidationModule();
        this.challengeService = new InMemoryChallengeService();
        this.module = new ValidationAndAuthenticationProcessingModule(fvm, challengeService);
        this.verifier = createEir("Verifier");
        this.target = createEir("Target");
    }

    @Override
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateChallengeForTarget() throws Exception {
        ChallengeRecord cr = module.createChallengeForTarget(target, verifier, CHALLENGE_TYPE);
        Assert.assertNotNull(cr);
        Assert.assertNotNull(cr.getId());
        Assert.assertNotNull(cr.getVaeId());
        Assert.assertEquals(CHALLENGE_TYPE, cr.getType());
        Assert.assertNotNull(cr.getChallenge());
        Assert.assertNotNull(cr.getTimestamp());
        Assert.assertFalse(challengeService.isProcessed(cr.getVaeId()));
    }

    @Test
    public void testCreateChallengeForVerifier() throws Exception {
        ChallengeRecord targetCr = module.createChallengeForTarget(target, verifier, CHALLENGE_TYPE);
        ChallengeRecord verifierCr = module.createChallengeForVerifier(targetCr, CHALLENGE_TYPE);

        Assert.assertNotNull(verifierCr);
        Assert.assertNotNull(verifierCr.getId());
        Assert.assertNotNull(verifierCr.getVaeId());
        Assert.assertEquals(CHALLENGE_TYPE, verifierCr.getType());
        Assert.assertNotNull(verifierCr.getChallenge());
        Assert.assertNotNull(verifierCr.getTimestamp());
        Assert.assertTrue(challengeService.isProcessed(verifierCr.getVaeId()));
    }

    @Test
    public void testVerifierCanNotBeNull() throws Exception {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Verifier EIR is null");
        module.createChallengeForTarget(target, null, CHALLENGE_TYPE);
    }

    @Test
    public void testTargetCanNotBeNull() throws Exception {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Target EIR is null");
        module.createChallengeForTarget(null, verifier, CHALLENGE_TYPE);
    }

    @Test
    public void testFormalValidationFails() throws Exception {
        FormalValidationModule formalValidationModule = new DummyFormalValidationModule(false);
        ValidationAndAuthenticationProcessingModule module = new ValidationAndAuthenticationProcessingModule(formalValidationModule, challengeService);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("formal validation failed");
        module.createChallengeForTarget(target, verifier, CHALLENGE_TYPE);
    }

    @Test
    public void testTargetAndVerifierCanNotBeEqual() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("target EIR == verifier EIR");
        module.createChallengeForTarget(target, target, CHALLENGE_TYPE);
    }

}