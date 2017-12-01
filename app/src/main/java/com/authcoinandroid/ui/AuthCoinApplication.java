package com.authcoinandroid.ui;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.authcoinandroid.model.Models;

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

    @Override
    public void onCreate() {
        super.onCreate();
        // override onUpgrade to handle migrating to a new version
        DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 1);
        if (BuildConfig.DEBUG) {
            // use this in development mode to drop and recreate the tables on every upgrade
            source.setTableCreationMode(TableCreationMode.DROP_CREATE);
        }
        Configuration configuration = source.getConfiguration();
        dataStore = ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));
    }

    /**
     * Returns {@link io.requery.sql.EntityDataStore} single instance for the application.
     */
    ReactiveEntityStore<Persistable> getDataStore() {
        return dataStore;
    }

}
