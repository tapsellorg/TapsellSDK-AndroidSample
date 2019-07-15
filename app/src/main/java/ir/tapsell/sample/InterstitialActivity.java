package ir.tapsell.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.enums.AdType;
import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;

import static ir.tapsell.sdk.TapsellAdRequestOptions.CACHE_TYPE_STREAMED;

public class InterstitialActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "InterstitialActivity";
    private Button btnInterstitialBanner, btnInterstitialVideo, btnShowAd;
    private TapsellAd ad = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        btnInterstitialBanner = findViewById(R.id.btnInterstitialBanner);
        btnInterstitialVideo = findViewById(R.id.btnInterstitialVideo);
        btnShowAd = findViewById(R.id.btnShowAd);

        btnInterstitialBanner.setOnClickListener(this);
        btnInterstitialVideo.setOnClickListener(this);
        btnShowAd.setOnClickListener(this);
        btnShowAd.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInterstitialBanner:
                requestInterstitialBannerAd(AdType.BANNER);
                break;
            case R.id.btnInterstitialVideo:
                requestInterstitialBannerAd(AdType.VIDEO);
                break;
            case R.id.btnShowAd:
                showAd();
                break;
        }
    }

    private void requestInterstitialBannerAd(AdType adType) {
        TapsellAdRequestOptions options = new TapsellAdRequestOptions(CACHE_TYPE_STREAMED);
        Tapsell.requestAd(this,
                adType == AdType.BANNER ? BuildConfig.TAPSELL_INTERSTITIAL_BANNER :
                        BuildConfig.TAPSELL_INTERSTITIAL_VIDEO, options, new TapsellAdRequestListener() {
                    @Override
                    public void onError(String s) {
                        Log.d(TAG, "on ad Error");
                    }

                    @Override
                    public void onAdAvailable(TapsellAd tapsellAd) {
                        Log.d(TAG, "on ad AdAvailable");
                        if (isDestroyed()) {
                            return;
                        }
                        ad = tapsellAd;
                        btnShowAd.setEnabled(true);
                    }

                    @Override
                    public void onNoAdAvailable() {
                        Log.d(TAG, "on ad NoAdAvailable");
                    }

                    @Override
                    public void onNoNetwork() {
                        Log.d(TAG, "on ad NoNetwork");
                    }

                    @Override
                    public void onExpiring(TapsellAd tapsellAd) {
                        Log.d(TAG, "on ad Expiring");
                    }
                });
    }

    private void showAd() {
        if (ad != null) {
            TapsellShowOptions showOptions = new TapsellShowOptions();
            showOptions.setRotationMode(TapsellShowOptions.ROTATION_LOCKED_PORTRAIT);

            ad.show(this, showOptions, new TapsellAdShowListener() {
                @Override
                public void onOpened(TapsellAd tapsellAd) {
                    Log.d(TAG, "on ad opened");
                }

                @Override
                public void onClosed(TapsellAd tapsellAd) {
                    Log.d(TAG, "on ad closed");
                }
            });
        } else {
            Log.e(TAG, "ad is not available");
        }

        btnShowAd.setEnabled(false);
        ad = null;
    }
}