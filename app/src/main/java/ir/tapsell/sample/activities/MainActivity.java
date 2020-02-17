package ir.tapsell.sample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.R;
import ir.tapsell.sample.activities.navideAds.NativeActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnVast = findViewById(R.id.btnVast);
        Button btnReward = findViewById(R.id.btnReward);
        Button btnStandard = findViewById(R.id.btnStandard);
        Button btnNative = findViewById(R.id.btnNative);
        Button btnInterstitial = findViewById(R.id.btnInterstitial);

        btnVast.setOnClickListener(this);
        btnReward.setOnClickListener(this);
        btnStandard.setOnClickListener(this);
        btnNative.setOnClickListener(this);
        btnInterstitial.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInterstitial:
                openActivity(InterstitialActivity.class);
                break;

            case R.id.btnNative:
                openActivity(NativeActivity.class);
                break;

            case R.id.btnStandard:
                openActivity(StandardActivity.class);
                break;

            case R.id.btnReward:
                openActivity(RewardActivity.class);
                break;

            case R.id.btnVast:
                openActivity(VastActivity.class);
                break;
        }
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
