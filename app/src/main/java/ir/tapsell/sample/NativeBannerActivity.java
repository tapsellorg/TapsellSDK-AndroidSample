package ir.tapsell.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import ir.tapsell.sdk.AdRequestCallback;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerManager;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerViewManager;


public class NativeBannerActivity extends AppCompatActivity {
    private FrameLayout adContainer;
    private TapsellNativeBannerViewManager nativeBannerViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_banner);

        adContainer = findViewById(R.id.adContainer);
        initTapsellNative();
        getTapsellAd();
    }

    private void initTapsellNative() {
        nativeBannerViewManager = new TapsellNativeBannerManager.Builder()
                .setParentView(adContainer)
                .setContentViewTemplate(R.layout.tapsell_content_banner_ad_template)
                .setAppInstallationViewTemplate(R.layout.tapsell_app_installation_banner_ad_template)
                .inflateTemplate(this);
    }

    private void getTapsellAd() {
        TapsellNativeBannerManager.getAd(this, BuildConfig.tapsellNativeBannerZoneId,
                new AdRequestCallback() {
                    @Override
                    public void onResponse(String[] adId) {
                        onAdResponse(adId);
                    }

                    @Override
                    public void onFailed(String message) {
                        Log.e("getTapsellAd onFailed", message);
                    }
                });
    }

    private void onAdResponse(final String[] adId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TapsellNativeBannerManager.bindAd(
                        NativeBannerActivity.this,
                        nativeBannerViewManager,
                        BuildConfig.tapsellNativeBannerZoneId,
                        adId[0]);
            }
        });
    }
}
