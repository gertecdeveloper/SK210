package com.example.sk210.util;

import android.app.Application;
import android.content.Context;

public class PriceScanApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
