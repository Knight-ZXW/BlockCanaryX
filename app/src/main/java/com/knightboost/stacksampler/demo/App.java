package com.knightboost.stacksampler.demo;

import android.app.Application;

import blockcanary.BlockCanary;
import blockcanary.BlockCanaryConfig;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BlockCanary.INSTANCE
                .install(this, BlockCanaryConfig.newBuilder().build());
    }
}
