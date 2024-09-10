package ir.tapsell.sample.activities.navideAds;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sample.utils.Tools;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAd;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoadListener;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoader;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdShowListener;

public class NativeVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "NativeVideoActivity";
    private FrameLayout adContainer;
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_video);

        initView();
    }

    private void initView() {
        Button btnRequest = findViewById(R.id.btnRequest);
        adContainer = findViewById(R.id.adContainer);
        tvLog = findViewById(R.id.tvLog);

        btnRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        requestNativeVideoAd();
    }

    private void requestNativeVideoAd() {
        new TapsellNativeVideoAdLoader.Builder()
                .setContentViewTemplate(R.layout.tapsell_content_video_ad_template)
                .setAutoStartVideoOnScreenEnabled(false)
                .setFullscreenBtnEnabled(true)
                .setMuteVideoBtnEnabled(false)
                .setMuteVideo(false)
                .loadAd(NativeVideoActivity.this, BuildConfig.TAPSELL_NATIVE_VIDEO, new TapsellNativeVideoAdLoadListener() {
                    @Override
                    public void onNoNetwork() {
                        if (Tools.isActivityDestroyed(NativeVideoActivity.this)) {
                            return;
                        }

                        Log.d(TAG, "No Network Available");
                        tvLog.append("\nonNoNetwork");
                    }

                    @Override
                    public void onNoAdAvailable() {
                        if (Tools.isActivityDestroyed(NativeVideoActivity.this)) {
                            return;
                        }

                        Log.d(TAG, "No Native Video Ad Available");
                        tvLog.append("\nonNoAdAvailable");
                    }

                    @Override
                    public void onError(String error) {
                        if (Tools.isActivityDestroyed(NativeVideoActivity.this)) {
                            return;
                        }

                        Log.d(TAG, "Error: " + error);
                        tvLog.append("\nonError " + error);
                    }

                    @Override
                    public void onRequestFilled(TapsellNativeVideoAd tapsellNativeVideoAd) {
                        if (Tools.isActivityDestroyed(NativeVideoActivity.this)) {
                            return;
                        }

                        Log.d(TAG, "onRequestFilled");
                        tvLog.append("\nonRequestFilled");

                        tapsellNativeVideoAd.setShowListener(new TapsellNativeVideoAdShowListener() {
                            @Override
                            public void onAdFinished(String adId) {
                                Log.e(TAG, "onAdShowFinished: " + adId);
                                tvLog.append("\nonAdShowFinished");
                            }

                            @Override
                            public void onAdClicked() {
                                Log.e(TAG, "onAdClicked: ");
                                tvLog.append("\nonAdClicked");
                            }
                        });

                        tapsellNativeVideoAd.addToParentView(adContainer);
                    }
                });

    }
}
