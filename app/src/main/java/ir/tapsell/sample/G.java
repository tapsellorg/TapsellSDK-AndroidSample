package ir.tapsell.sample;

import android.app.Application;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellConfiguration;

/**
 * Created by Mohammad Hadipour
 */

public class G extends Application {

    // default app

    public static final String testAppKey = "kilkhmaqckffopkpfnacjkobgrgnidkphkcbtmbcdhiokqetigljpnnrbfbnpnhmeikjbq";

    public static final String videoZoneId = "5a5dbd5cc21bf000010d1686";
    public static final String interstitialBannerZoneId = "5a3f5063fca4f000014b70a8";
    public static final String nativeVideoZoneId = "5953bd064684652dd8fcb02e";
    public static final String nativeBannerZoneId = "5953bc774684652dd8fc937e";
    public static final String vastZoneId = "5953bb294684652dd8fc4f9f";
    public static final String standardBannerZoneId = "5a28f47539086d0001670416";

    // admin test app

//    public static final String testAppKey = "datqahaefslnfobpqpanedcfhehinntfotjkghmsfbdachsnhqpeqidosiaakqecofimqk";
//
//    public static final String videoZoneId = "586e4ed9bc5c28712bd8d9c9";
//    public static final String interstitialBannerZoneId = "592422a8468465344dc89e92";
//    public static final String nativeVideoZoneId = "59e760264684653519633d4d";
//    public static final String nativeBannerZoneId = "59ae63b9468465682dff1390";
//    public static final String vastZoneId = "59e75fe0468465351963266b";
//    public static final String standardBannerZoneId = "5a478b4259196f000187291d";


    @Override
    public void onCreate() {
        super.onCreate();

        TapsellConfiguration config = new TapsellConfiguration(this);
        config.setDebugMode(true);
        config.setPermissionHandlerMode(TapsellConfiguration.PERMISSION_HANDLER_DISABLED);
        Tapsell.initialize(this, config, testAppKey);

        Tapsell.setMaxAllowedBandwidthUsagePercentage(this, 100);

    }
}
