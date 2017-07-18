package ir.tapsell.samplev3;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import ir.tapsell.samplev3.utils.TapsellConstants;
import ir.tapsell.sdk.nativeads.android.TapsellNativeBannerAdLoadListener;
import ir.tapsell.sdk.nativeads.android.TapsellNativeBannerAdLoader;

public class NativeBannerActivity extends Activity {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_banner);

        LinearLayout adParent = (LinearLayout) findViewById(R.id.adParent);

        new TapsellNativeBannerAdLoader.Builder()
                .setContentViewTemplate(R.layout.tapsell_content_banner_ad_template)
                .setAppInstallationViewTemplate(R.layout.tapsell_app_installation_banner_ad_template)
                .loadAd(NativeBannerActivity.this, TapsellConstants.nativeBannerZoneId, adParent, new TapsellNativeBannerAdLoadListener() {

                    @Override
                    public void onNoNetwork() {
                        Log.e("Tapsell","No Network Available");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NativeBannerActivity.this,"No Network Available",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onNoAdAvailable() {
                        Log.e("Tapsell","No Native Banner Ad Available!");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NativeBannerActivity.this,"No Native Banner Ad Available",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onError(final String error) {
                        Log.e("Tapsell","Error: "+error);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NativeBannerActivity.this,"Error: "+error,Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onRequestFilled(View adView, ViewGroup parentView) {
                        Log.e("Tapsell","Native Banner AdView Available");
                        adView.setBackgroundColor(Color.rgb(255,255,220));
                        parentView.addView(adView);
                        (adView.findViewById(R.id.tapsell_nativead_description)).setSelected(true);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NativeBannerActivity.this,"onRequestFilled",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
    }
}
