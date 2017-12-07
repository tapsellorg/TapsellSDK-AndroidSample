package ir.tapsell.samplev3;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PersistableBundle;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;

import ir.tapsell.samplev3.utils.TapsellConstants;
import ir.tapsell.samplev3.views.SampleVideoPlayer;
import ir.tapsell.sdk.vast.TapsellVast;

public class ExoplayerVastSample extends Activity implements AdErrorEvent.AdErrorListener, AdEvent.AdEventListener {

    public static final String VIDEO_URL = "videoUrl";
    private static final String VIDEO_PAUSED = "videoPaused";
    private static final String VIDEO_PAUSED_POSITION = "videoPausedPosition";
    private String videoUrl=null;

    //    private SequenceVideoView mVideoView;
    private SampleVideoPlayer mVideoView;
    private boolean videoPaused = false;
    private int videoPausedPosition = 0;

    private ImaSdkFactory mSdkFactory;
    private AdsLoader mAdsLoader;
    private AdsManager mAdsManager;
    private boolean mIsAdDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer_vast_sample);

        videoUrl = "https://tapsell.ir/wp-content/uploads/2017/07/avc_avc_avc_Front_video_v4.mp4";

        mVideoView = (SampleVideoPlayer) findViewById(R.id.svvPlayer);
        intializeIMA();
        initializeVideoPlayer();
        startVideo();
    }

    private void intializeIMA()
    {
        // Create an AdsLoader.
        mSdkFactory = ImaSdkFactory.getInstance();
        mAdsLoader = mSdkFactory.createAdsLoader(ExoplayerVastSample.this);
        // Add listeners for when ads are loaded and for errors.
        mAdsLoader.addAdErrorListener(this);
        mAdsLoader.addAdsLoadedListener(new AdsLoader.AdsLoadedListener() {
            @Override
            public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
                // Ads were successfully loaded, so get the AdsManager instance. AdsManager has
                // events for ad playback and errors.
                mAdsManager = adsManagerLoadedEvent.getAdsManager();

                // Attach event and error event listeners.
                mAdsManager.addAdErrorListener(ExoplayerVastSample.this);
                mAdsManager.addAdEventListener(ExoplayerVastSample.this);
                mAdsManager.init();
            }
        });

        // Add listener for when the content video finishes.
        mVideoView.addVideoCompletedListener(new SampleVideoPlayer.OnVideoCompletedListener() {
            @Override
            public void onVideoCompleted() {
                // Handle completed event for playing post-rolls.
                if (mAdsLoader != null) {
                    mAdsLoader.contentComplete();
                }
            }
        });

        // When Play is clicked, request ads and hide the button.
//        mPlayButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mVideoPlayer.setVideoPath(getString(R.string.content_url));
//                requestAds(getString(R.string.ad_tag_url));
//                view.setVisibility(View.GONE);
//            }
//        });

    }

    private void initializeVideoPlayer()
    {
        mVideoView.setVideoURI(Uri.parse(videoUrl));
//        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                finish();
//            }
//        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
//                if (what == 100)
//                {
//                    //noinspection EmptyCatchBlock
//                    try{
//                        mVideoView.stopPlayback();
//                        startVideo();
//                    }catch (Throwable te)
//                    {
//                    }
//                }
                finish();
                return false;
            }
        });
    }

    private void startVideo()
    {
        AdDisplayContainer adDisplayContainer = mSdkFactory.createAdDisplayContainer();
        adDisplayContainer.setAdContainer((FrameLayout) findViewById(R.id.adUiContainer));

        // Create the ads request.
        AdsRequest request = mSdkFactory.createAdsRequest();
        String adTagUrl = TapsellVast.getVastUrl(ExoplayerVastSample.this,
                TapsellConstants.prerollZoneId,
                TapsellVast.PREROLL_TYPE_LONG,
                TapsellVast.VAST_VERSION_3);
        Log.e("TapsellVast IMA SDK","TapsellVast Ad Tag Url: "+adTagUrl);
        request.setAdTagUrl(adTagUrl);
        request.setAdDisplayContainer(adDisplayContainer);
        request.setContentProgressProvider(new ContentProgressProvider() {
            @Override
            public VideoProgressUpdate getContentProgress() {
                if (mIsAdDisplayed || mVideoView == null || mVideoView.getDuration() <= 0) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new VideoProgressUpdate(mVideoView.getCurrentPosition(),
                        mVideoView.getDuration());
            }
        });

        // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
        mAdsLoader.requestAds(request);


//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mVideoView.start();
////                mVideoView.updateVideoSize();
//                mVideoView.setOnPreparedListener(null);
//            }
//        });
//        mVideoView.setVideoURI(Uri.parse(videoUrl));
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(videoPaused)
//        {
//            videoPaused = false;
//            if(mVideoView!=null)
//            {
////                Logger.LogError("resume video, videoPausedPosition="+videoPausedPosition);
//                if(videoPausedPosition>0)
//                {
////                    mVideoView.seekTo(videoPausedPosition, new MediaPlayer.OnSeekCompleteListener() {
////                        @Override
////                        public void onSeekComplete(MediaPlayer mp) {
//////                            Logger.LogError("resume video, onSeekComplete");
////                            mVideoView.start();
////                        }
////                    });
//                }
//
//            }
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mVideoView != null && mVideoView.isPlaying()) {
//            videoPausedPosition = mVideoView.getCurrentPosition();
////            Logger.LogError("pause videoView in onPause, videoPausePosition="+videoPausedPosition);
//            mVideoView.pause();
//            videoPaused = true;
//        }
//    }

    @Override
    public void onResume() {
        if (mAdsManager != null && mIsAdDisplayed) {
            mAdsManager.resume();
        } else {
            mVideoView.play();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mAdsManager != null && mIsAdDisplayed) {
            mAdsManager.pause();
        } else {
            mVideoView.pause();
        }
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (videoUrl != null) {
            outState.putString(VIDEO_URL, videoUrl);
        }
        outState.putBoolean(VIDEO_PAUSED, videoPaused);
        outState.putInt(VIDEO_PAUSED_POSITION, videoPausedPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreInstanceStateFromBundle(savedInstanceState);
    }

    private void restoreInstanceStateFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(VIDEO_URL)) {
                videoUrl = savedInstanceState.getString(VIDEO_URL);
            }
            if (savedInstanceState.containsKey(VIDEO_PAUSED)) {
                videoPaused = savedInstanceState.getBoolean(VIDEO_PAUSED);
            }
            if (savedInstanceState.containsKey(VIDEO_PAUSED_POSITION)) {
                videoPausedPosition = savedInstanceState.getInt(VIDEO_PAUSED_POSITION);
            }
        }
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        mVideoView.play();
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        // These are the suggested event types to handle. For full list of all ad event
        // types, see the documentation for AdEvent.AdEventType.
        switch (adEvent.getType()) {
            case LOADED:
                // AdEventType.LOADED will be fired when ads are ready to be played.
                // AdsManager.start() begins ad playback. This method is ignored for VMAP or
                // ad rules playlists, as the SDK will automatically start executing the
                // playlist.
                mAdsManager.start();
                break;
            case CONTENT_PAUSE_REQUESTED:
                // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before a video
                // ad is played.
                mIsAdDisplayed = true;
                mVideoView.pause();
                break;
            case CONTENT_RESUME_REQUESTED:
                // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is completed
                // and you should start playing your content.
                mIsAdDisplayed = false;
                mVideoView.play();
                break;
            case ALL_ADS_COMPLETED:
                if (mAdsManager != null) {
                    mAdsManager.destroy();
                    mAdsManager = null;
                }
                break;
            default:
                break;
        }
    }

}
