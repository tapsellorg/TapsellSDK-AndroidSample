package ir.tapsell.sample.prerollAds;

import android.content.Context;
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

public class VideoFragment extends Fragment {

    private VideoPlayerController mVideoPlayerController;
    private TextView mVideoTitle;
    private LinearLayout mVideoExampleLayout;
    private OnVideoFragmentViewCreatedListener mViewCreatedCallback;

    @Override
    public void onAttach(Context context) {
        try {
            mViewCreatedCallback = (OnVideoFragmentViewCreatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + OnVideoFragmentViewCreatedListener.class.getName());
        }
        super.onAttach(context);
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

    private void loadVideo() {
        if (mVideoPlayerController == null) {
            return;
        }
        mVideoPlayerController.setContentVideo(
                "https://storage.backtory.com/tapsell-server/sdk/VASTContentVideo.mp4");
        mVideoPlayerController.setAdTagUrl(TapsellVast.getAdTag(this.getContext(),
                BuildConfig.TAPSELL_PRE_ROL_VIDEO, TapsellVast.PREROLL_TYPE_BOTH,
                TapsellVast.VAST_VERSION_3));
    }

    private void initUi(View rootView) {
        VideoPlayerWithAdPlayback mVideoPlayerWithAdPlayback =
                rootView.findViewById(R.id.videoPlayerWithAdPlayback);
        View playButton = rootView.findViewById(R.id.playButton);
        View playPauseToggle = rootView.findViewById(R.id.videoContainer);
        mVideoTitle = rootView.findViewById(R.id.video_title);
        mVideoExampleLayout = rootView.findViewById(R.id.videoExampleLayout);

        final TextView logText = rootView.findViewById(R.id.logText);
        final ScrollView logScroll = rootView.findViewById(R.id.logScroll);

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

        loadVideo();
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

    public interface OnVideoFragmentViewCreatedListener {
        void onVideoFragmentViewCreated();
    }
}
