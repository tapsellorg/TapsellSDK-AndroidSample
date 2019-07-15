package ir.tapsell.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;

import static ir.tapsell.sdk.TapsellAdRequestOptions.CACHE_TYPE_STREAMED;

public class RewardActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "RewardActivity";
    private Button btnRewardVideo,btnShowAd;
    private TapsellAd ad = null;

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
        setContentView(R.layout.activity_reward);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnRewardVideo = findViewById(R.id.btnRewardVideo);
        btnShowAd = findViewById(R.id.btnShowAd);
        btnRewardVideo.setOnClickListener(this);
        btnShowAd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRewardVideo:
                requestAd();
                break;
            case R.id.btnShowAd:
                showAd();
                break;
        }
    }

    private void requestAd() {
        TapsellAdRequestOptions options = new TapsellAdRequestOptions(CACHE_TYPE_STREAMED);
        Tapsell.requestAd(this, BuildConfig.TAPSELL_REWARDED_VIDEO, options,
                new TapsellAdRequestListener() {
                    @Override
                    public void onError(String s) {
                        Log.d(TAG, "on ad Error");
                    }

                    @Override
                    public void onAdAvailable(TapsellAd tapsellAd) {
                        Log.d(TAG, "on ad AdAvailable");
                        if (isDestroyed()) {
                            return;
                        }
                        ad = tapsellAd;
                    }

                    @Override
                    public void onNoAdAvailable() {
                        Log.d(TAG, "on ad NoAdAvailable");
                    }

                    @Override
                    public void onNoNetwork() {
                        Log.d(TAG, "on ad NoNetwork");
                    }

                    @Override
                    public void onExpiring(TapsellAd tapsellAd) {
                        Log.d(TAG, "on ad Expiring");
                    }
                });
    }

    private void showAd() {
        if (ad != null) {
            TapsellShowOptions showOptions = new TapsellShowOptions();
            showOptions.setRotationMode(TapsellShowOptions.ROTATION_LOCKED_PORTRAIT);

            ad.show(this, showOptions, new TapsellAdShowListener() {
                @Override
                public void onOpened(TapsellAd tapsellAd) {
                    Log.d(TAG, "on ad opened");
                }

                @Override
                public void onClosed(TapsellAd tapsellAd) {
                    Log.d(TAG, "on ad closed");
                }
            });
        } else {
            Log.e(TAG, "ad is not available");
        }

        ad = null;
    }
}
