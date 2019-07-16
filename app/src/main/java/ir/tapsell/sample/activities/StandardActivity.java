package ir.tapsell.sample.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.R;
import ir.tapsell.sdk.bannerads.TapsellBannerType;
import ir.tapsell.sdk.bannerads.TapsellBannerView;

public class StandardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard);
    }
}
