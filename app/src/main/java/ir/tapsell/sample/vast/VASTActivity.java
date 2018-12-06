package ir.tapsell.sample.vast;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import ir.tapsell.sample.R;


public class VASTActivity extends AppCompatActivity
    implements VideoFragment.OnVideoFragmentViewCreatedListener {

    private static final String VIDEO_EXAMPLE_FRAGMENT_TAG = "video_example_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vast);

        // The video list fragment won't exist for phone layouts, so add it dynamically so we can
        // .replace() it once the user selects a video.

        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoFragment videoFragment = new VideoFragment();
        if (fragmentManager.findFragmentByTag(VIDEO_EXAMPLE_FRAGMENT_TAG) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.video_example_container, videoFragment,
                            VIDEO_EXAMPLE_FRAGMENT_TAG)
                    .commit();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        orientAppUi();
    }

    private void orientAppUi() {
        int orientation = getResources().getConfiguration().orientation;
        boolean isLandscape = (orientation == Configuration.ORIENTATION_LANDSCAPE);
        // Hide the non-video content when in landscape so the video is as large as possible.
        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoFragment videoFragment = (VideoFragment) fragmentManager
                .findFragmentByTag(VIDEO_EXAMPLE_FRAGMENT_TAG);

        if (videoFragment != null) {
            // If the video playlist is onscreen (tablets) then hide that fragment.
            videoFragment.makeFullscreen(isLandscape);
            if (isLandscape) {
                hideStatusBar();
            } else {
                showStatusBar();
            }
        }
    }

    @Override
    public void onVideoFragmentViewCreated() {
        orientAppUi();
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT >= 16) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }
    }

    private void showStatusBar() {
        if (Build.VERSION.SDK_INT >= 16) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            getSupportActionBar().show();
        }
    }
}
