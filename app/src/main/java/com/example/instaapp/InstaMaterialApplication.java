package com.example.instaapp;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Abdo GHazi
 */
public class InstaMaterialApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
