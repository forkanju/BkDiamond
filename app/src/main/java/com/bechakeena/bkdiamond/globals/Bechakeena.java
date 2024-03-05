package com.bechakeena.bkdiamond.globals;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import static com.bechakeena.bkdiamond.globals.Constants.DB_NAME;

public class Bechakeena extends Application {

    private static Bechakeena mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        initRealm();
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration defaultRealmConfiguration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(DB_NAME)
                .build();
        Realm.setDefaultConfiguration(defaultRealmConfiguration);
    }

    public static Bechakeena getInstance() {
        return mInstance;
    }

}
