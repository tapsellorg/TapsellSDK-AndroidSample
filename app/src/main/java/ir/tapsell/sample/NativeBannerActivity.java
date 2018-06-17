package ir.tapsell.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import ir.tapsell.sdk.nativeads.TapsellNativeBannerAd;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerAdLoadListener;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerAdLoader;


public class NativeBannerActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_banner);

        new TapsellNativeBannerAdLoader.Builder()
                .setContentViewTemplate(R.layout.tapsell_content_banner_ad_template)
                .setAppInstallationViewTemplate(R.layout.tapsell_app_installation_banner_ad_template)
                .loadAd(NativeBannerActivity.this, BuildConfig.tapsellNativeBannerZoneId, new TapsellNativeBannerAdLoadListener() {

                    @Override
                    public void onNoNetwork() {
                        Log.e("Tapsell", "No Network Available");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NativeBannerActivity.this, "No Network Available", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onNoAdAvailable() {
                        Log.e("Tapsell", "No Native Banner Ad Available!");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NativeBannerActivity.this, "No Native Banner Ad Available", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onError(final String error) {
                        Log.e("Tapsell", "Error: " + error);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NativeBannerActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onRequestFilled(TapsellNativeBannerAd adV) {
                        TapsellNativeBannerAd ad = TapsellNativeBannerAd.createFromStateBundle(NativeBannerActivity.this,
                                adV.getStateBundle(),
                                R.layout.tapsell_content_banner_ad_template,
                                R.layout.tapsell_app_installation_banner_ad_template);

                        Log.e("Tapsell", "Native Banner AdView Available");
                        ad.addToParentView((LinearLayout) findViewById(R.id.adParent));
                        (ad.findViewById(R.id.tapsell_nativead_description)).setSelected(true);
                        Toast.makeText(NativeBannerActivity.this, "onRequestFilled", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
