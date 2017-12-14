package com.authcoinandroid.service.challenge;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.contract.AuthcoinContractService;
import com.authcoinandroid.service.identity.IdentityService;
import io.reactivex.Observable;
import org.web3j.abi.datatypes.Address;

import java.util.List;

import static com.authcoinandroid.service.qtum.mapper.RecordContractParamMapper.*;
import static com.authcoinandroid.util.ContractUtil.bytesToBytes32;


public class ChallengeServiceImpl implements ChallengeService {

    private ChallengeRepository challengeRepository;
    private AuthcoinContractService authcoinContractService;
    private IdentityService identityService;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository, AuthcoinContractService authcoinContractService, IdentityService identityService) {
        this.challengeRepository = challengeRepository;
        this.authcoinContractService = authcoinContractService;
        this.identityService = identityService;
    }

    @Override
    public void registerChallenge(ChallengeRecord challenge) {
        this.challengeRepository.save(challenge);
        // TODO send data to BC
    }

    @Override
    public boolean isProcessed(byte[] vaeId) {
        // TODO query DB
        return false;
    }

    /**
     * Note: this method may emit many lists of ChallengeRecords,
     * collect them all in onNext method and use them in onComplete
     *
     * @param eir EntityIdentityRecord whose CRs are needed
     * @return Observable containing list of {@link ChallengeRecord}
     */
    @Override
    public Observable<List<ChallengeRecord>> getChallengeRecordsForEir(EntityIdentityRecord eir) {
        /*
        * 1. Get all VAEs where eir is participant
        * 2. For each VAE get all challenge record ids
        * 3. For each challenge record id get challenge address
        * 4. For each challenge address get all data at that address
        */
        // get all VAE addresses
        return this.authcoinContractService.getVaeArrayByEirId(bytesToBytes32(eir.getId()))
                // parse VAE addresses
                .flatMap(response -> Observable.fromIterable(resolveAddressesFromAbiReturn(response.getItems().get(0).getOutput())))
                // get list of observables for address
                // TODO save to db
                .flatMap(this::getChallengeRecordsForVae);
    }

    private Observable<List<ChallengeRecord>> getChallengeRecordsForVae(Address address) {
        //  get ids of all challenges for VAE address
        return this.authcoinContractService.getChallengeIds(address)
                // parse response
                .flatMap(response -> Observable.fromIterable(resolveBytes32FromAbiReturn(response.getItems().get(0).getOutput())))
                // get challenge data by challenge by id
                .flatMap(challengeId -> this.authcoinContractService.getChallengeRecord(address, challengeId))
                // resolve data about challenge records
                .flatMap(response -> Observable.just(resolveCrFromAbiReturn(response.getItems().get(0).getOutput(), this.identityService)))
                .toList()
                .toObservable();
    }
}
