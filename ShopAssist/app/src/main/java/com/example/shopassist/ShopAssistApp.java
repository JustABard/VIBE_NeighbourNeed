package com.example.shopassist;

import android.app.Application;
import android.content.Context;

public class ShopAssistApp extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}

