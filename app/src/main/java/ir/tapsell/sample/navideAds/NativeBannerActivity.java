package ir.tapsell.sample.navideAds;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sdk.AdRequestCallback;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerManager;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerViewManager;

public class NativeBannerActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "NativeBannerActivity";
    private Button btnShow;
    private TapsellNativeBannerViewManager nativeBannerViewManager;
    private String[] adId = null;
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_banner);
        initView();
    }

    private void initView() {
        FrameLayout adContainer = findViewById(R.id.adContainer);
        Button btnRequest = findViewById(R.id.btnRequest);
        btnShow = findViewById(R.id.btnShow);
        tvLog = findViewById(R.id.tvLog);

        btnRequest.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnShow.setEnabled(false);

        nativeBannerViewManager = new TapsellNativeBannerManager.Builder()
                .setParentView(adContainer)
                .setContentViewTemplate(R.layout.tapsell_content_banner_ad_template)
                .inflateTemplate(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRequest:
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

                        if (isDestroyed()) {
                            return;
                        }

                        tvLog.append("\nonAdAvailable");
                        NativeBannerActivity.this.adId = adId;
                        btnShow.setEnabled(true);
                    }

                    @Override
                    public void onFailed(String message) {
                        if (isDestroyed()) {
                            return;
                        }

                        Log.d(TAG, "onFailed" + message);
                        tvLog.append("\nonFailed " + message);
                    }
                });
    }

    private void showAd() {
        if (adId == null) {
            return;
        }

        TapsellNativeBannerManager.bindAd(
                this,
                nativeBannerViewManager,
                BuildConfig.TAPSELL_NATIVE_BANNER,
                adId[0]);

        btnShow.setEnabled(false);
    }
}
