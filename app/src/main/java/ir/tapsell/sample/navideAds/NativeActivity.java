package ir.tapsell.sample.navideAds;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.R;

public class NativeActivity extends AppCompatActivity
        implements View.OnClickListener {

    Intent intent = new Intent();
    private Button btnNativeVideoInList, btnNativeVideoInActivity,
            btnNativeBannerInList, btnNativeBannerInActivity;

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
        setContentView(R.layout.activity_native);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        btnNativeVideoInList = findViewById(R.id.btnNativeVideoInList);
        btnNativeVideoInActivity = findViewById(R.id.btnNativeVideoInActivity);
        btnNativeBannerInList = findViewById(R.id.btnNativeBannerInList);
        btnNativeBannerInActivity = findViewById(R.id.btnNativeBannerInActivity);

        btnNativeVideoInList.setOnClickListener(this);
        btnNativeVideoInActivity.setOnClickListener(this);
        btnNativeBannerInList.setOnClickListener(this);
        btnNativeBannerInActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNativeVideoInList:
                intent = new Intent(this, NativeVideoInList.class);
                break;
            case R.id.btnNativeVideoInActivity:
                intent = new Intent(this, NativeVideoActivity.class);
                break;
            case R.id.btnNativeBannerInList:
                intent = new Intent(this, NativeBannerInListActivity.class);
                break;
            case R.id.btnNativeBannerInActivity:
                intent = new Intent(this, NativeBannerActivity.class);
                break;
        }
        startActivity(intent);
    }
}
