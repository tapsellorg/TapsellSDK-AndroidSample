package ir.tapsell.sample;

import android.app.Application;

import androidx.multidex.MultiDex;

import ir.tapsell.sdk.Tapsell;

public class TapsellApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Uncomment this part for testing on lower android versions
        //MultiDex.install(this);
        Tapsell.initialize(this, BuildConfig.TAPSELL_KEY);
    }
}
