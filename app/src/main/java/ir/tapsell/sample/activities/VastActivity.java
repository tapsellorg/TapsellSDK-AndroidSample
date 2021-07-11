package ir.tapsell.sample.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sdk.Tapsell;

public class VastActivity extends AppCompatActivity implements View.OnClickListener,
        AdEvent.AdEventListener, AdErrorEvent.AdErrorListener {

    private static final String TAG = "VASTActivity";
    // Whether an ad is displayed.
    private boolean isAdDisplayed;

    // The video player.
    private VideoView videoView;
    private TextView tvLog;
    // The play button to trigger the ad request.

    // The container for the ad's UI.
    private ViewGroup adUiContainer;

    // Factory class for creating SDK objects.
    private ImaSdkFactory imaSdkFactory;

    // The AdsLoader instance exposes the requestAds method.
    private AdsLoader adsLoader;

    // AdsManager exposes methods to control ad playback and listen to ad events.
    private AdsManager adsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vast);

        Button btnRequest = findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(this);


        videoView = findViewById(R.id.videoPlayer);
        adUiContainer = findViewById(R.id.videoPlayerWithAdPlayback);
        tvLog = findViewById(R.id.tvLog);

        // Create an AdsLoader.
        imaSdkFactory = ImaSdkFactory.getInstance();
        AdDisplayContainer adDisplayContainer =
                ImaSdkFactory.createAdDisplayContainer(
                        adUiContainer,
                        ImaSdkFactory.createSdkOwnedPlayer(this, adUiContainer));

        ImaSdkSettings settings = imaSdkFactory.createImaSdkSettings();
        settings.setLanguage("fa");
        adsLoader = imaSdkFactory.createAdsLoader(this, settings, adDisplayContainer);

        // Add listeners for when ads are loaded and for errors.
        adsLoader.addAdErrorListener(this);
        adsLoader.addAdsLoadedListener(
                adsManagerLoadedEvent -> {
                    // Ads were successfully loaded, so get the AdsManager instance.
                    // AdsManager has events for ad playback and errors.
                    adsManager = adsManagerLoadedEvent.getAdsManager();

                    // Attach event and error event listeners.
                    adsManager.addAdErrorListener(VastActivity.this);
                    adsManager.addAdEventListener(VastActivity.this);
                    adsManager.init();
                });

        // Add listener for when the content video finishes.
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                mediaPlayer.setDisplay(videoView.getHolder());

                if (adsLoader != null) {
                    adsLoader.contentComplete();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        videoView.setVideoPath(
                "https://storage.backtory.com/tapsell-server/sdk/VASTContentVideo.mp4");
        requestAds(Tapsell.getVastTag(BuildConfig.TAPSELL_VAST));
    }


    private void requestAds(String adTagUrl) {
        // Create the ads request.
        AdsRequest request = imaSdkFactory.createAdsRequest();
        request.setAdTagUrl(adTagUrl);
        request.setContentProgressProvider(
                new ContentProgressProvider() {
                    @Override
                    public VideoProgressUpdate getContentProgress() {
                        if (isAdDisplayed || videoView == null || videoView.getDuration() <= 0) {
                            return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                        }
                        return new VideoProgressUpdate(
                                videoView.getCurrentPosition(), videoView.getDuration());
                    }
                });

        // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
        adsLoader.requestAds(request);
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        Log.i(TAG, "Event: " + adEvent.getType());

        tvLog.append(adEvent.getType().name() + "\n");
        // These are the suggested event types to handle. For full list of all ad event
        // types, see the documentation for AdEvent.AdEventType.
        switch (adEvent.getType()) {
            case LOADED:
                // AdEventType.LOADED will be fired when ads are ready to be played.
                // AdsManager.start() begins ad playback. This method is ignored for VMAP or
                // ad rules playlists, as the SDK will automatically start executing the
                // playlist.
                adsManager.start();
                break;

            case CONTENT_PAUSE_REQUESTED:
                // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before a video
                // ad is played.
                isAdDisplayed = true;
                videoView.pause();
                break;

            case CONTENT_RESUME_REQUESTED:
                // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is completed
                // and you should start playing your content.
                isAdDisplayed = false;
                videoView.start();
                break;

            case ALL_ADS_COMPLETED:
                if (adsManager != null) {
                    adsManager.destroy();
                    adsManager = null;
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        tvLog.append(adErrorEvent.getError().getMessage() + "\n");
        Log.e(TAG, "Ad Error: " + adErrorEvent.getError().getMessage());
        videoView.start();
    }

    @Override
    public void onResume() {
        if (adsManager != null && isAdDisplayed) {
            adsManager.resume();
        } else {
            videoView.start();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (adsManager != null && isAdDisplayed) {
            adsManager.pause();
        } else {
            videoView.pause();
        }
        super.onPause();
    }
}
