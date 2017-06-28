package ir.tapsell.samplev3;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ir.tapsell.samplev3.utils.TapsellConstants;
import ir.tapsell.sdk.nativeads.android.TapsellNativeVideoAdLoadListener;
import ir.tapsell.sdk.nativeads.android.TapsellNativeVideoAdLoader;

public class NativeVideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_video);

        LinearLayout adParent = (LinearLayout) findViewById(R.id.adParent);

        new TapsellNativeVideoAdLoader.Builder()
                .setContentViewTemplate(R.layout.tapsell_content_video_ad_template)
                .setAppInstallationViewTemplate(R.layout.tapsell_app_installation_video_ad_template)
                .setAutoStartVideoOnScreenEnabled(false)
                .loadAd(NativeVideoActivity.this, TapsellConstants.nativeVideoZoneId, adParent, new TapsellNativeVideoAdLoadListener() {

                    @Override
                    public void onNoNetwork() {
                        Log.d("Tapsell","No Network Available");
                    }

                    @Override
                    public void onNoAdAvailable() {
                        Log.d("Tapsell","No Native Video Ad Available");
                    }

                    @Override
                    public void onError(String error) {
                        Log.d("Tapsell","Error: "+error);
                    }

                    @Override
                    public void onRequestFilled(View adView, ViewGroup parentView) {
                        adView.setBackgroundColor(Color.WHITE);
                        parentView.addView(adView);
                    }
                });
    }
}
