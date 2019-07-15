package ir.tapsell.sample;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sdk.bannerads.TapsellBannerType;
import ir.tapsell.sdk.bannerads.TapsellBannerView;

public class StandardActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStandardBanner;
    private TapsellBannerView bannerView;
    private FrameLayout adContainer;

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
        setContentView(R.layout.activity_standard);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnStandardBanner = findViewById(R.id.btnStandardBanner);
        bannerView = findViewById(R.id.bannerView);
        adContainer = findViewById(R.id.adContainer);

        btnStandardBanner.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStandardBanner:
                loadAd();
                break;
        }
    }

    private void loadAd() {
        bannerView.loadAd(
                this,
                BuildConfig.TAPSELL_STANDARD_BANNER,
                TapsellBannerType.BANNER_320x50
        );
        inflateDynamicalBannerView();
    }

    private void inflateDynamicalBannerView() {
        TapsellBannerView banner = new TapsellBannerView(
                this,
                TapsellBannerType.BANNER_320x50,
                BuildConfig.TAPSELL_STANDARD_BANNER
        );

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER_HORIZONTAL;
        banner.setLayoutParams(params);

        adContainer.addView(banner);
    }
}
