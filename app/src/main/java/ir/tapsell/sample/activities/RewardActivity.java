package ir.tapsell.sample.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sample.utils.Tools;
import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class RewardActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "RewardActivity";
    private TextView tvLog;
    private String adId;
    private String zoneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        initView();
    }

    private void initView() {
        Button btnRequest = findViewById(R.id.btnRequest);
        Button btnShow = findViewById(R.id.btnShow);
        tvLog = findViewById(R.id.tvLog);

        btnRequest.setOnClickListener(this);
        btnShow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRequest:
                requestAd();
                break;

            case R.id.btnShow:
                showAd();
                break;
        }
    }

    private void requestAd() {
        Tapsell.requestAd(this,
                BuildConfig.TAPSELL_REWARDED_VIDEO,
                new TapsellAdRequestOptions(),
                new TapsellAdRequestListener() {

                    @Override
                    public void onAdAvailable(String adId) {
                        Log.d(TAG, "on ad AdAvailable");
                        RewardActivity.this.adId = adId;
                        RewardActivity.this.zoneId = BuildConfig.TAPSELL_REWARDED_VIDEO;
                        if (Tools.isActivityDestroyed(RewardActivity.this)) {
                            return;
                        }

                        tvLog.append("\nonAdAvailable");
                    }

                    @Override
                    public void onError(String message) {
                        if (Tools.isActivityDestroyed(RewardActivity.this)) {
                            return;
                        }
                        Log.d(TAG, "on ad Error" + message);
                        tvLog.append("\nonError " + message);
                    }
                });
    }

    private void showAd() {
        Tapsell.showAd(this,
                zoneId,
                adId,
                new TapsellShowOptions(),
                new TapsellAdShowListener() {
                    @Override
                    public void onOpened() {
                        Log.d(TAG, "on ad opened");
                        tvLog.append("\nonOpened");
                    }

                    @Override
                    public void onClosed() {
                        Log.d(TAG, "on ad closed");
                        tvLog.append("\nonClosed");
                    }

                    @Override
                    public void onError(String message) {
                        Log.d(TAG, "on error " + message);
                        tvLog.append("\non error " + message);
                    }

                    @Override
                    public void onRewarded(boolean completed) {
                        Log.d(TAG, "on Rewarded " + completed);
                        tvLog.append("\non Rewarded " + completed);
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Log.d(TAG, "onAdClicked");
                        tvLog.append("\nonAdClicked ");
                    }
                });
    }

}
