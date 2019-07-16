package ir.tapsell.sample.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class InterstitialActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "InterstitialActivity";
    private Button btnShowAd;
    private TextView tvLog;
    private TapsellAd ad = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        initView();
    }

    private void initView() {
        Button btnInterstitialBanner = findViewById(R.id.btnInterstitialBanner);
        Button btnInterstitialVideo = findViewById(R.id.btnInterstitialVideo);

        btnShowAd = findViewById(R.id.btnShowAd);
        tvLog = findViewById(R.id.tvLog);

        btnInterstitialBanner.setOnClickListener(this);
        btnInterstitialVideo.setOnClickListener(this);

        btnShowAd.setOnClickListener(this);
        btnShowAd.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInterstitialBanner:
                requestInterstitialBannerAd(BuildConfig.TAPSELL_INTERSTITIAL_BANNER);
                break;

            case R.id.btnInterstitialVideo:
                requestInterstitialBannerAd(BuildConfig.TAPSELL_INTERSTITIAL_VIDEO);
                break;

            case R.id.btnShowAd:
                showAd();
                break;
        }
    }

    private void requestInterstitialBannerAd(String zoneId) {
        Tapsell.requestAd(this,
                zoneId,
                new TapsellAdRequestOptions(),
                new TapsellAdRequestListener() {
                    @Override
                    public void onAdAvailable(TapsellAd tapsellAd) {
                        Log.d(TAG, "on ad AdAvailable");
                        if (isDestroyed()) {
                            return;
                        }

                        tvLog.append("\nonAdAvailable");
                        ad = tapsellAd;
                        btnShowAd.setEnabled(true);
                    }

                    @Override
                    public void onError(String message) {
                        if (isDestroyed()) {
                            return;
                        }
                        Log.d(TAG, "on ad Error" + message);
                        tvLog.append("\nonError " + message);
                    }

                    @Override
                    public void onNoAdAvailable() {
                        if (isDestroyed()) {
                            return;
                        }
                        Log.d(TAG, "on ad NoAdAvailable");
                        tvLog.append("\nonNoAdAvailable");
                    }

                    @Override
                    public void onNoNetwork() {
                        if (isDestroyed()) {
                            return;
                        }
                        Log.d(TAG, "on ad NoNetwork");
                        tvLog.append("\nonNoNetwork");
                    }

                    @Override
                    public void onExpiring(TapsellAd tapsellAd) {
                        if (isDestroyed()) {
                            return;
                        }
                        Log.d(TAG, "on ad Expiring");
                        tvLog.append("\nonExpiring");
                    }
                });
    }

    private void showAd() {
        if (ad == null) {
            Log.e(TAG, "ad is not available");
            tvLog.append("\nad is not available");
            return;
        }

        ad.show(this,
                new TapsellShowOptions(),
                new TapsellAdShowListener() {
                    @Override
                    public void onOpened(TapsellAd tapsellAd) {
                        Log.d(TAG, "on ad opened");
                        tvLog.append("\nonOpened");
                    }

                    @Override
                    public void onClosed(TapsellAd tapsellAd) {
                        Log.d(TAG, "on ad closed");
                        tvLog.append("\nonClosed");
                    }
                });

        ad = null;
        btnShowAd.setEnabled(false);
    }
}