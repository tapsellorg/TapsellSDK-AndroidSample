package ir.tapsell.sample.vast;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sdk.vast.TapsellVast;

/**
 * The main fragment for displaying video content.
 */
public class VideoFragment extends Fragment {

    private VideoPlayerController mVideoPlayerController;
    private TextView mVideoTitle;
    private LinearLayout mVideoExampleLayout;

    private OnVideoFragmentViewCreatedListener mViewCreatedCallback;

    /**
     * Listener called when the fragment's onCreateView is fired.
     */
    public interface OnVideoFragmentViewCreatedListener {
        public void onVideoFragmentViewCreated();
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mViewCreatedCallback = (OnVideoFragmentViewCreatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + OnVideoFragmentViewCreatedListener.class.getName());
        }
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        initUi(rootView);
        if (mViewCreatedCallback != null) {
            mViewCreatedCallback.onVideoFragmentViewCreated();
        }
        return rootView;
    }

    public void loadVideo() {
        if (mVideoPlayerController == null) {
            return;
        }
        mVideoPlayerController.setContentVideo("https://storage.backtory.com/tapsell-server/sdk/VASTContentVideo.mp4");
        mVideoPlayerController.setAdTagUrl(TapsellVast.getAdTag(this.getContext(), BuildConfig.tapsellVastZoneId, TapsellVast.PREROLL_TYPE_BOTH, TapsellVast.VAST_VERSION_3));
    }

    private void initUi(View rootView) {
        VideoPlayerWithAdPlayback mVideoPlayerWithAdPlayback = (VideoPlayerWithAdPlayback)
                rootView.findViewById(R.id.videoPlayerWithAdPlayback);
        View playButton = rootView.findViewById(R.id.playButton);
        View playPauseToggle = rootView.findViewById(R.id.videoContainer);
        mVideoTitle = rootView.findViewById(R.id.video_title);
        mVideoExampleLayout = rootView.findViewById(R.id.videoExampleLayout);

        final TextView logText = rootView.findViewById(R.id.logText);
        final ScrollView logScroll = rootView.findViewById(R.id.logScroll);

        // Provide an implementation of a logger so we can output SDK events to the UI.
        VideoPlayerController.Logger logger = new VideoPlayerController.Logger() {
            @Override
            public void log(String message) {
                if (logText != null) {
                    logText.append(message);
                }
                if (logScroll != null) {
                    logScroll.post(new Runnable() {
                        @Override
                        public void run() {
                            logScroll.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        };

        mVideoPlayerController = new VideoPlayerController(this.getActivity(),
                mVideoPlayerWithAdPlayback, playButton, playPauseToggle,
                "fa", logger);

        // If we've already selected a video, load it now.
        loadVideo();
    }

    /**
     * Shows or hides all non-video UI elements to make the video as large as possible.
     */
    public void makeFullscreen(boolean isFullscreen) {
        for (int i = 0; i < mVideoExampleLayout.getChildCount(); i++) {
            View view = mVideoExampleLayout.getChildAt(i);
            // If it's not the video element, hide or show it, depending on fullscreen status.
            if (view.getId() != R.id.videoContainer) {
                if (isFullscreen) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public VideoPlayerController getVideoPlayerController() {
        return mVideoPlayerController;
    }

    @Override
    public void onPause() {
        if (mVideoPlayerController != null) {
            mVideoPlayerController.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mVideoPlayerController != null) {
            mVideoPlayerController.resume();
        }
        super.onResume();
    }

}
