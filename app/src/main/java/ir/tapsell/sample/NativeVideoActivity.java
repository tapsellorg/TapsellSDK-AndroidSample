package ir.tapsell.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import ir.tapsell.sdk.nativeads.TapsellNativeVideoAd;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdCompletionListener;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoadListener;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoader;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoIconSet;

public class NativeVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_video);

        LinearLayout adParent = (LinearLayout) findViewById(R.id.adParent);

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
                .loadAd(NativeVideoActivity.this, G.nativeVideoZoneId, new TapsellNativeVideoAdLoadListener() {

                    @Override
                    public void onNoNetwork() {
                        Log.e("Tapsell", "No Network Available");
                    }

                    @Override
                    public void onNoAdAvailable() {
                        Log.e("Tapsell", "No Native Video Ad Available");
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("Tapsell", "Error: " + error);
                    }

                    @Override
                    public void onRequestFilled(TapsellNativeVideoAd tapsellNativeVideoAd) {
                        tapsellNativeVideoAd.setCompletionListener(new TapsellNativeVideoAdCompletionListener() {
                            @Override
                            public void onAdShowFinished(String adId) {
                                Log.e("Tapsell","onAdShowFinished: "+adId);
                            }
                        });
                        tapsellNativeVideoAd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e("Tapsell","Native video clicked!");
                            }
                        });
                        tapsellNativeVideoAd.addToParentView((LinearLayout) findViewById(R.id.adParent));
                    }

                });
    }
}
