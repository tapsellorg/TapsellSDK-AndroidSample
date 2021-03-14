package ir.tapsell.sample.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sdk.bannerads.TapsellBannerType;
import ir.tapsell.sdk.bannerads.TapsellBannerView;
import ir.tapsell.sdk.bannerads.TapsellBannerViewEventListener;

public class StandardActivity extends AppCompatActivity {

    private TapsellBannerView tapsellBannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard);

        tapsellBannerView = findViewById(R.id.bannerView);
        tapsellBannerView.setEventListener(new TapsellBannerViewEventListener() {
            @Override
            public void onNoAdAvailable() {

                Log.e("onNoAdAvailable", "called!");
            }

            @Override
            public void onNoNetwork() {

                Log.e("onNoNetwork", "called!");
            }

            @Override
            public void onError(String errorMessage) {

                Log.e("onError", errorMessage);
            }

            @Override
            public void onRequestFilled() {

                Log.e("onRequestFilled", "called!");
            }

            @Override
            public void onHideBannerView() {

                Log.e("onHideBannerView", "called!");
            }
        });

        tapsellBannerView.loadAd(StandardActivity.this, BuildConfig.TAPSELL_STANDARD_BANNER, TapsellBannerType.BANNER_320x50);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tapsellBannerView.destroy();
    }
}
