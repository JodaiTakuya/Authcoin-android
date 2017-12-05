package com.authcoinandroid.ui;

import android.support.multidex.MultiDexApplication;

import com.authcoinandroid.model.Models;
import com.authcoinandroid.service.challenge.ChallengeRepository;
import com.authcoinandroid.service.identity.EirRepository;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

import io.requery.Persistable;
import io.requery.android.BuildConfig;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;

/**
 * Base class for maintaining global AuthCoin application state.
 */
public class AuthCoinApplication extends MultiDexApplication {

    /**
     * AuthCoin application data store. We should have one data store per application.
     */
    private ReactiveEntityStore<Persistable> dataStore;
    private EirRepository eirRepository;
    private ChallengeRepository challengeRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        // override onUpgrade to handle migrating to a new version
        DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 3);
        if (BuildConfig.DEBUG) {
            // use this in development mode to drop and recreate the tables on every upgrade
            source.setTableCreationMode(TableCreationMode.DROP_CREATE);
        }

        Configuration configuration = source.getConfiguration();
        this.dataStore = ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));

        // creat repositories
        this.eirRepository = new EirRepository(dataStore);
        this.challengeRepository = new ChallengeRepository(dataStore);
    }

    /**
     * Returns {@link io.requery.sql.EntityDataStore} single instance for the application.
     */
    public ReactiveEntityStore<Persistable> getDataStore() {
        return dataStore;
    }


    public EirRepository getEirRepository() {
        return eirRepository;
    }
}
