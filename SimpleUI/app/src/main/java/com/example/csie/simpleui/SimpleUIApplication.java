package com.example.csie.simpleui;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by csie on 2015/3/23.
 */
public class SimpleUIApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this,
                "ucTD3rqecD1jHY8NGDHDUhrypkYeiVYxfb7bYjGH",
                "0FQy4fMeCHavguzxpuLGUqFXW4G2j1D6ruilPInS");
    }
}
