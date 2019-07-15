package ir.tapsell.sample;

import android.app.Application;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellConfiguration;

public class TapsellApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TapsellConfiguration config = new TapsellConfiguration(this);
        config.setDebugMode(true);
        Tapsell.initialize(this, config, BuildConfig.TAPSELL_KEY);
    }
}
