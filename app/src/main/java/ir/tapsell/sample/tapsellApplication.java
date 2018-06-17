package ir.tapsell.sample;

import android.app.Application;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellConfiguration;

public class tapsellApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TapsellConfiguration config = new TapsellConfiguration(this);
        config.setPermissionHandlerMode(TapsellConfiguration.PERMISSION_HANDLER_DISABLED);
        Tapsell.initialize(this, config, BuildConfig.tapsellSampleAppKey);
    }
}
