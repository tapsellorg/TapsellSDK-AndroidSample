package ir.tapsell.sample.navideAds;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ir.tapsell.sample.R;
import ir.tapsell.sample.adapter.NativeVideoAdapter;

public class NativeVideoInList extends AppCompatActivity {

    private RecyclerView rvItems;
    private Button btnNativeVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_video_in_list);

        initView();
    }

    private void initView() {
        btnNativeVideo = findViewById(R.id.btnNativeVideo);
        rvItems = findViewById(R.id.rvItems);

        rvItems.setLayoutManager(new LinearLayoutManager(NativeVideoInList.this,
                RecyclerView.VERTICAL, false));

        btnNativeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvItems.setAdapter(new NativeVideoAdapter(NativeVideoInList.this));
            }
        });
    }
}
