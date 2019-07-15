package ir.tapsell.sample.navideAds;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sdk.AdRequestCallback;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerManager;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerViewManager;

public class NativeBannerActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "NativeBannerActivity";
    private FrameLayout adContainer;
    private Button btnNativeBanner, btnShow;
    private TapsellNativeBannerViewManager nativeBannerViewManager;
    private String[] adId = null;

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
        setContentView(R.layout.activity_native_banner);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
    }

    private void initView() {
        adContainer = findViewById(R.id.adContainer);
        btnNativeBanner = findViewById(R.id.btnNativeBanner);
        btnShow = findViewById(R.id.btnShow);
        btnNativeBanner.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnShow.setEnabled(false);

        nativeBannerViewManager = new TapsellNativeBannerManager.Builder()
                .setParentView(adContainer)
                .setContentViewTemplate(R.layout.tapsell_content_banner_ad_template)
                .setAppInstallationViewTemplate(R.layout.tapsell_app_installation_banner_ad_template)
                .inflateTemplate(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNativeBanner:
                requestNativeBannerAd();
                break;
            case R.id.btnShow:
                showAd();
                break;
        }
    }

    private void requestNativeBannerAd() {
        TapsellNativeBannerManager.getAd(this, BuildConfig.TAPSELL_NATIVE_BANNER,
                new AdRequestCallback() {
                    @Override
                    public void onResponse(String[] adId) {
                        Log.d(TAG, "onResponse");
                        NativeBannerActivity.this.adId = adId;
                        btnShow.setEnabled(true);
                    }

                    @Override
                    public void onFailed(String message) {
                        Log.d(TAG, "onFailed: " + message);
                    }
                });
    }

    private void showAd() {
        if (adId != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TapsellNativeBannerManager.bindAd(
                            NativeBannerActivity.this,
                            nativeBannerViewManager,
                            BuildConfig.TAPSELL_NATIVE_BANNER,
                            adId[0]);
                }
            });
        }
        btnShow.setEnabled(false);
    }
}
