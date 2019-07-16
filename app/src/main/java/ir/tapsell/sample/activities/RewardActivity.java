package ir.tapsell.sample.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellRewardListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class RewardActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "RewardActivity";
    private Button btnShow;
    private TextView tvLog;
    private TapsellAd ad = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        initView();

        Tapsell.setRewardListener(new TapsellRewardListener() {
            @Override
            public void onAdShowFinished(TapsellAd tapsellAd, boolean completed) {
                Log.d(TAG, "onAdShowFinished completed? " + completed);
                tvLog.append("\nonAdShowFinished completed? " + completed);
            }
        });
    }

    private void initView() {
        Button btnRequest = findViewById(R.id.btnRequest);
        btnShow = findViewById(R.id.btnShow);
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
                    public void onAdAvailable(TapsellAd tapsellAd) {
                        Log.d(TAG, "on ad AdAvailable");
                        if (isDestroyed()) {
                            return;
                        }

                        tvLog.append("\nonAdAvailable");
                        ad = tapsellAd;
                        btnShow.setEnabled(true);
                    }

                    @Override
                    public void onError(String message) {
                        if (isDestroyed()) {
                            return;
                        }
                        Log.d(TAG, "on ad Error" + message);
                        tvLog.append("\nonError " + message);
                    }

                    @Override
                    public void onNoAdAvailable() {
                        if (isDestroyed()) {
                            return;
                        }
                        Log.d(TAG, "on ad NoAdAvailable");
                        tvLog.append("\nonNoAdAvailable");
                    }

                    @Override
                    public void onNoNetwork() {
                        if (isDestroyed()) {
                            return;
                        }
                        Log.d(TAG, "on ad NoNetwork");
                        tvLog.append("\nonNoNetwork");
                    }

                    @Override
                    public void onExpiring(TapsellAd tapsellAd) {
                        if (isDestroyed()) {
                            return;
                        }
                        Log.d(TAG, "on ad Expiring");
                        tvLog.append("\nonExpiring");
                    }
                });
    }

    private void showAd() {
        if (ad == null) {
            Log.e(TAG, "ad is not available");
            tvLog.append("\nad is not available");
            return;
        }

        ad.show(this,
                new TapsellShowOptions(),
                new TapsellAdShowListener() {
                    @Override
                    public void onOpened(TapsellAd tapsellAd) {
                        Log.d(TAG, "on ad opened");
                        tvLog.append("\nonOpened");
                    }

                    @Override
                    public void onClosed(TapsellAd tapsellAd) {
                        Log.d(TAG, "on ad closed");
                        tvLog.append("\nonClosed");
                    }
                });

        ad = null;
        btnShow.setEnabled(false);
    }

}
