package ir.tapsell.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.navideAds.NativeBannerActivity;
import ir.tapsell.sample.prerollAds.PreRollActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPreRoll, btnReward, btnStandard, btnNative, btnInterstitial;
    Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPreRoll = findViewById(R.id.btnPreRoll);
        btnReward = findViewById(R.id.btnReward);
        btnStandard = findViewById(R.id.btnStandard);
        btnNative = findViewById(R.id.btnNative);
        btnInterstitial = findViewById(R.id.btnInterstitial);

        btnPreRoll.setOnClickListener(this);
        btnReward.setOnClickListener(this);
        btnStandard.setOnClickListener(this);
        btnNative.setOnClickListener(this);
        btnInterstitial.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInterstitial:
                intent = new Intent(MainActivity.this, InterstitialActivity.class);
                break;

            case R.id.btnNative:
                intent = new Intent(MainActivity.this, NativeBannerActivity.class);
                break;

            case R.id.btnStandard:
                intent = new Intent(MainActivity.this, StandardActivity.class);
                break;

            case R.id.btnReward:
                intent = new Intent(MainActivity.this, RewardActivity.class);
                break;

            case R.id.btnPreRoll:
                intent = new Intent(MainActivity.this, PreRollActivity.class);
                break;
        }
        startActivity(intent);
    }
}
