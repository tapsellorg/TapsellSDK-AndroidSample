package ir.tapsell.sample.prerollAds;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ir.tapsell.sample.R;

public class PreRollActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String VIDEO_EXAMPLE_FRAGMENT_TAG = "video_example_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_roll);

        Button btnPreRoll = findViewById(R.id.btnPreRoll);
        btnPreRoll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        initView();
    }

    private void initView() {
        if (getSupportFragmentManager().findFragmentByTag(VIDEO_EXAMPLE_FRAGMENT_TAG) == null) {
            getSupportFragmentManager().beginTransaction().add(
                    R.id.adContainer,
                    new VideoFragment(),
                    VIDEO_EXAMPLE_FRAGMENT_TAG
            ).commit();
        }
    }
}
