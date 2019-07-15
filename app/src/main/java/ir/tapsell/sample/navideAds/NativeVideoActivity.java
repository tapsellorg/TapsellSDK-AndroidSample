package ir.tapsell.sample.navideAds;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAd;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdCompletionListener;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoadListener;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoader;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoIconSet;

public class NativeVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "NativeVideoActivity";
    private Button btnNativeVideo, btnShow;
    private LinearLayout adParent;

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
        setContentView(R.layout.activity_native_video);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
    }

    private void initView() {
        btnNativeVideo = findViewById(R.id.btnNativeVideo);
        adParent = findViewById(R.id.adParent);
        btnShow = findViewById(R.id.btnShow);
        btnNativeVideo.setOnClickListener(this);
        btnShow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNativeVideo:
                requestNativeVideoAd();
                break;
            case R.id.btnShow:

                break;
        }
    }

    private void requestNativeVideoAd() {
        new TapsellNativeVideoAdLoader.Builder()
                .setContentViewTemplate(R.layout.tapsell_content_video_ad_template)
                .setAppInstallationViewTemplate(R.layout.tapsell_app_installation_video_ad_template)
                .setAutoStartVideoOnScreenEnabled(false)
                .setFullscreenBtnEnabled(true)
                .setMuteVideoBtnEnabled(false)
                .setMuteVideo(false)
                .setIconSet(new TapsellNativeVideoIconSet.Builder()
                        .setFullscreenIcon(R.drawable.full2)
                        .setPlayIcon(R.drawable.play2)
                        .create())
                .loadAd(NativeVideoActivity.this, BuildConfig.TAPSELL_NATIVE_VIDEO, new TapsellNativeVideoAdLoadListener() {
                    @Override
                    public void onNoNetwork() {
                        Log.d(TAG, "No Network Available");
                    }

                    @Override
                    public void onNoAdAvailable() {
                        Log.d(TAG, "No Native Video Ad Available");
                    }

                    @Override
                    public void onError(String error) {
                        Log.d(TAG, "Error: " + error);
                    }

                    @Override
                    public void onRequestFilled(TapsellNativeVideoAd tapsellNativeVideoAd) {
                        tapsellNativeVideoAd.setCompletionListener(new TapsellNativeVideoAdCompletionListener() {
                            @Override
                            public void onAdShowFinished(String adId) {
                                Log.e(TAG, "onAdShowFinished: " + adId);
                            }
                        });
                        tapsellNativeVideoAd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e(TAG, "Native video clicked!");
                            }
                        });
                        tapsellNativeVideoAd.addToParentView(adParent);
                    }
                });

    }
}
