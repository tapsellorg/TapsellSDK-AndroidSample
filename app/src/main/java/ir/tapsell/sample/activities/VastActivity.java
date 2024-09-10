package ir.tapsell.sample.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.preroll.TapsellPrerollAd;
import ir.tapsell.sdk.preroll.TapsellPrerollAdsLoaderListener;
import ir.tapsell.sdk.preroll.ima.ImaAdsLoader;

public class VastActivity extends AppCompatActivity {

    private static final String TAG = "VastActivity";
    private static final String SAMPLE_VIDEO_URL = "https://storage.backtory.com/tapsell-server/sdk/VASTContentVideo.mp4";

    private TapsellPrerollAd tapsellPrerollAd;
    private ImaAdsLoader adsLoader;
    private PlayerView playerView;
    private ExoPlayer exoPlayer;
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vast);

        ViewGroup adUiContainer = findViewById(R.id.video_player_container);
        ViewGroup companionContainer = findViewById(R.id.companion_ad_slot);
        playerView = findViewById(R.id.exo_player);

        tvLog = findViewById(R.id.tvLog);
        Button btnRequest = findViewById(R.id.btnRequest);

        tapsellPrerollAd = new TapsellPrerollAd.Builder(this)
                .setVideoPlayer(playerView)
                .setVideoPath(SAMPLE_VIDEO_URL)
                .setAdContainer(adUiContainer)
                .setCompanionContainer(companionContainer)
                .addAdsLoaderListener(new TapsellPrerollAdsLoaderListener() {
                    @Override
                    public void onAdsLoaderCreated(ImaAdsLoader adsLoader) {
                        Log.d(TAG, "onAdsLoaderCreated");
                        VastActivity.this.adsLoader = adsLoader;
                        initializePlayer();
                    }
                })
                .addEventListener(adEvent -> {
                            Log.i(TAG, "Event: " + adEvent.getType());
                            tvLog.append(adEvent.getType().name() + "\n");
                        }
                ).addErrorListener(adErrorEvent -> {
                            Log.e(TAG, "Ad Error: " + adErrorEvent.getError().getMessage());
                            tvLog.append(adErrorEvent.getError().getMessage() + "\n");
                        }
                ).build();


        btnRequest.setOnClickListener(v -> requestAd());
    }

    private void initializePlayer() {
        if (adsLoader == null) {
            Log.w(TAG, "initializePlayer failed: adsLoader is null");
            return;
        }

        if (exoPlayer != null) {
            releasePlayer();
        }

        // Set up the factory for media sources, passing the ads loader and ad view providers.
        DefaultMediaSourceFactory mediaSourceFactory =
                new DefaultMediaSourceFactory(this)
                        .setLocalAdInsertionComponents(unusedAdTagUri -> adsLoader, playerView);

        // Create an ExoPlayer and set it as the player for content and ads.
        exoPlayer = new ExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build();
        playerView.setPlayer(exoPlayer);
        adsLoader.setPlayer(exoPlayer);

        // Set PlayWhenReady. If true, content and ads will autoplay.
        exoPlayer.setPlayWhenReady(true);
    }


    private void requestAd() {
        tvLog.setText("");
        Uri contentUri = Uri.parse(SAMPLE_VIDEO_URL);
        Uri adTagUri = Uri.parse(Tapsell.getVastTag(BuildConfig.TAPSELL_VAST));

        MediaItem.AdsConfiguration adsConfiguration = new MediaItem.AdsConfiguration.Builder(adTagUri).build();
        MediaItem mediaItem = new MediaItem.Builder().setUri(contentUri).setAdsConfiguration(adsConfiguration).build();
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }

    private void releasePlayer() {
        playerView.setPlayer(null);
        exoPlayer.release();
        exoPlayer = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (exoPlayer == null) {
            initializePlayer();
        }
        if (playerView != null) {
            playerView.onResume();
        }
        if (tapsellPrerollAd != null) {
            tapsellPrerollAd.resumeAd();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        playerView.onPause();
        tapsellPrerollAd.pauseAd();
    }

    @Override
    protected void onDestroy() {
        tapsellPrerollAd.destroy();
        releasePlayer();
        super.onDestroy();
    }
}