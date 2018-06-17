package ir.tapsell.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ir.tapsell.sdk.bannerads.TapsellBannerType;
import ir.tapsell.sdk.bannerads.TapsellBannerView;

public class WebBannerActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_banner);

        TapsellBannerView banner1 = findViewById(R.id.banner1);

        banner1.loadAd(WebBannerActivity.this, BuildConfig.tapsellStandardBannerZoneId, TapsellBannerType.BANNER_320x100);
    }
}
