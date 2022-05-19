package com.knightboost.stacksampler.demo;

import android.app.Application;
import android.os.Debug;
import android.os.Trace;

import androidx.core.os.TraceCompat;

import blockcanary.BlockCanary;
import blockcanary.BlockCanaryConfig;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BlockCanary.INSTANCE
                .install(this,
                        BlockCanaryConfig.newBuilder().build());
    }
}
