// Copyright 2014 Google Inc. All Rights Reserved.

package ir.tapsell.sample.vast;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import ir.tapsell.sdk.vast.AdEvent;
import ir.tapsell.sdk.vast.TapsellVast;
import ir.tapsell.sdk.vast.TapsellVastAdsListener;

/**
 * Ads logic for handling the IMA SDK integration code and events.
 */
public class VideoPlayerController {

    /**
     * Log interface, so we can output the log commands to the UI or similar.
     */
    public interface Logger {
        void log(String logMessage);
    }

    TapsellVast tapsellVast;

    // Ad-enabled video player.
    private VideoPlayerWithAdPlayback mVideoPlayerWithAdPlayback;

    // Button the user taps to begin video playback and ad request.
    private View mPlayButton;

    // VAST ad tag URL to use when requesting ads during video playback.
    private String mCurrentAdTagUrl;

    // URL of content video.
    private String mContentVideoUrl;

    // ViewGroup to render an associated companion ad into.
    private ViewGroup mCompanionViewGroup;

    // Tracks if the SDK is playing an ad, since the SDK might not necessarily use the video
    // player provided to play the video ad.
    private boolean mIsAdPlaying;

    // View that handles taps to toggle ad pause/resume during video playback.
    private View mPlayPauseToggle;

    // View that we can write log messages to, to display in the UI.
    private Logger mLog;


    public VideoPlayerController(Context context,
            VideoPlayerWithAdPlayback videoPlayerWithAdPlayback, View playButton,
            View playPauseToggle, String language, Logger log) {
        mVideoPlayerWithAdPlayback = videoPlayerWithAdPlayback;
        mPlayButton = playButton;
        mPlayPauseToggle = playPauseToggle;
        mIsAdPlaying = false;
        mLog = log;

        // Create an AdsLoader and optionally set the language.
        tapsellVast = new TapsellVast(context, language);

        tapsellVast.setTapsellVastAdsListener(new TapsellVastAdsListener() {
            @Override
            public void onAdError(String message) {
                log("Ad Error: " + message);
                resumeContent();
            }

            @Override
            public void onAdEvent(AdEvent adEvent) {
                log("Ad Event: " + adEvent);
                switch (adEvent) {
                    case CONTENT_PAUSE_REQUESTED:
                        // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before
                        // a video ad is played.
                        pauseContent();
                        break;
                    case CONTENT_RESUME_REQUESTED:
                        // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is
                        // completed and you should start playing your content.
                        resumeContent();
                        break;
                    case PAUSED:
                        mIsAdPlaying = false;
                        break;
                    case RESUMED:
                        mIsAdPlaying = true;
                        break;
                    default:
                        break;
                }
            }
        });


        mVideoPlayerWithAdPlayback.setOnContentCompleteListener(
                new VideoPlayerWithAdPlayback.OnContentCompleteListener() {
                    /**
                     * Event raised by VideoPlayerWithAdPlayback when content video is complete.
                     */
                    @Override
                    public void onContentComplete() {
                        tapsellVast.contentComplete();
                    }
                });

        // When Play is clicked, request ads and hide the button.
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAndPlayAds();
            }
        });
    }

    private void log(String message) {
        if (mLog != null) {
            mLog.log(message + "\n");
        }
    }

    private void pauseContent() {
        mVideoPlayerWithAdPlayback.pauseContentForAdPlayback();
        mIsAdPlaying = true;
        setPlayPauseOnAdTouch();
    }

    private void resumeContent() {
        mVideoPlayerWithAdPlayback.resumeContentAfterAdPlayback();
        mIsAdPlaying = false;
        removePlayPauseOnAdTouch();
    }

    /**
     * Set the ad tag URL the player should use to request ads when playing a content video.
     */
    public void setAdTagUrl(String adTagUrl) {
        mCurrentAdTagUrl = adTagUrl;
    }

    public String getAdTagUrl() {
        return mCurrentAdTagUrl;
    }

    /**
     * Request and subsequently play video ads from the ad server.
     */
    public void requestAndPlayAds() {
        if (mCurrentAdTagUrl == null || mCurrentAdTagUrl == "") {
            log("No VAST ad tag URL specified");
            resumeContent();
            return;
        }

        mPlayButton.setVisibility(View.GONE);
//        tapsellVast.setVideoAdPlayer(mVideoPlayerWithAdPlayback.getVideoAdPlayer());
        tapsellVast.requestAds(mVideoPlayerWithAdPlayback.getVideoAdPlayer(), mVideoPlayerWithAdPlayback.getAdUiContainer(),
                mCurrentAdTagUrl, mVideoPlayerWithAdPlayback.getContentProgressProvider());
    }

    /**
     * Touch to toggle play/pause during ad play instead of seeking.
     */
    private void setPlayPauseOnAdTouch() {
        // Use AdsManager pause/resume methods instead of the video player pause/resume methods
        // in case the SDK is using a different, SDK-created video player for ad playback.
        mPlayPauseToggle.setOnTouchListener(
                new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent event) {
                        // If an ad is playing, touching it will toggle playback.
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if (mIsAdPlaying) {
                                tapsellVast.pause();
                            } else {
                                tapsellVast.resume();
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );
    }

    /**
     * Remove the play/pause on touch behavior.
     */
    private void removePlayPauseOnAdTouch() {
        mPlayPauseToggle.setOnTouchListener(null);
    }

    /**
     * Set metadata about the content video. In more complex implementations, this might
     * more than just a URL and could trigger additional decisions regarding ad tag selection.
     */
    public void setContentVideo(String videoPath) {
        mVideoPlayerWithAdPlayback.setContentVideoPath(videoPath);
        mContentVideoUrl = videoPath;
    }

    public String getContentVideoUrl() {
        return mContentVideoUrl;
    }

    /**
     * Save position of the video, whether content or ad. Can be called when the app is
     * paused, for example.
     */
    public void pause() {
        mVideoPlayerWithAdPlayback.savePosition();
        if (tapsellVast != null && mVideoPlayerWithAdPlayback.getIsAdDisplayed()) {
            tapsellVast.pause();
        } else {
            mVideoPlayerWithAdPlayback.pause();
        }
    }

    /**
     * Restore the previously saved progress location of the video. Can be called when
     * the app is resumed.
     */
    public void resume() {
        mVideoPlayerWithAdPlayback.restorePosition();
        if (tapsellVast != null && mVideoPlayerWithAdPlayback.getIsAdDisplayed()) {
            tapsellVast.resume();
        } else {
            mVideoPlayerWithAdPlayback.play();
        }
    }

    /**
     * Seeks to time in content video in seconds.
     */
    public void seek(double time) {
        mVideoPlayerWithAdPlayback.seek((int) (time * 1000.0));
    }

    /**
     * Returns the current time of the content video in seconds.
     */
    public double getCurrentContentTime() {
        return ((double) mVideoPlayerWithAdPlayback.getCurrentContentTime()) / 1000.0;
    }
}
