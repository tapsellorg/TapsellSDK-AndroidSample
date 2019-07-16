package ir.tapsell.sample.navideAds;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.R;
import ir.tapsell.sample.prerollAds.PreRollActivity;

public class NativeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        initView();
    }

    private void initView() {
        Button btnNativeBannerInActivity = findViewById(R.id.btnNativeBannerInActivity);
        Button btnNativeBannerInList = findViewById(R.id.btnNativeBannerInList);
        Button btnNativeVideoInActivity = findViewById(R.id.btnNativeVideoInActivity);

        btnNativeBannerInActivity.setOnClickListener(this);
        btnNativeBannerInList.setOnClickListener(this);
        btnNativeVideoInActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnNativeBannerInActivity:
                openActivity(NativeBannerActivity.class);
                break;

            case R.id.btnNativeBannerInList:
                openActivity(NativeBannerInListActivity.class);
                break;

            case R.id.btnNativeVideoInActivity:
                openActivity(NativeVideoActivity.class);
                break;

        }
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
