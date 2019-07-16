package ir.tapsell.sample;

import android.app.Application;

import ir.tapsell.sdk.Tapsell;

public class TapsellApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Tapsell.initialize(this, BuildConfig.TAPSELL_KEY);
    }
}
