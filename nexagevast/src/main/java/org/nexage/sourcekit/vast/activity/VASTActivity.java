//
//  VastActivity.java
//

//  Copyright (c) 2014 Nexage. All rights reserved.
//

package org.nexage.sourcekit.vast.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.nexage.sourcekit.util.Assets;
import org.nexage.sourcekit.util.HttpTools;
import org.nexage.sourcekit.util.VASTLog;
import org.nexage.sourcekit.vast.R;
import org.nexage.sourcekit.vast.VASTPlayer;
import org.nexage.sourcekit.vast.model.TRACKING_EVENTS_TYPE;
import org.nexage.sourcekit.vast.model.VASTModel;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class VASTActivity extends Activity implements OnCompletionListener,
		OnErrorListener, OnPreparedListener, OnVideoSizeChangedListener,
		SurfaceHolder.Callback {

	private static String TAG = "VASTActivity";
//	public static final int VAST_PLAYER_RESULT_CODE = 1;
//	public static final String VAST_PLAYER_RETURN_MESSAGE_CODE = "RETURN MESSAGE";

	private static final double SKIP_INFO_PADDING_SCALE = 0.10;
	private static final double SKIP_INFO_SCALE = 0.15;

	// timer delays
	private static final long TOOLBAR_HIDE_DELAY = 3000;
	private static final long QUARTILE_TIMER_INTERVAL = 250;
	private static final long VIDEO_PROGRESS_TIMER_INTERVAL = 250;

	
	// timers
	private Timer mToolBarTimer;
	private Timer mTrackingEventTimer;
	private Timer mStartVideoProgressTimer;

	private LinkedList<Integer> mVideoProgressTracker = null;
	private final int mMaxProgressTrackingPoints = 20;

	private Handler mHandler;

	private VASTModel mVastModel = null;
	private HashMap<TRACKING_EVENTS_TYPE, List<String>> mTrackingEventMap;

	private MediaPlayer mMediaPlayer;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private RelativeLayout mOverlay;
	private RelativeLayout mRootLayout;
	private RelativeLayout mButtonPanel;

	private ImageButton mInfoButton;
	private ImageButton mPlayPauseButton;
	private ImageButton mCloseButton;

	private Drawable mPauseDrawable;
	private Drawable mPlayDrawable;
	private int mVideoHeight;
	private int mVideoWidth;
	private int mScreenWidth;
	private int mScreenHeight;	
	private boolean mIsVideoPaused = false;
	private boolean mIsPlayBackError = false;
	private boolean mIsProcessedImpressions = false;
	private boolean mIsCompleted = false;
	private int mCurrentVideoPosition;
	private int mQuartile = 0;
	
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		VASTLog.d(TAG, "in onCreate");
		super.onCreate(savedInstanceState);

		int currentOrientation = this.getResources().getConfiguration().orientation;
		VASTLog.d(TAG, "currentOrientation:" + currentOrientation);

		if (currentOrientation != Configuration.ORIENTATION_LANDSCAPE) {
			VASTLog.d(TAG,
					"Orientation is not landscape.....forcing landscape");
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		} else {
			VASTLog.d(TAG, "orientation is landscape");
			Intent i = getIntent();
			mVastModel = (VASTModel) i
					.getSerializableExtra("com.nexage.android.vast.player.vastModel");
			if (mVastModel == null) {
				VASTLog.e(TAG, "vastModel is null. Stopping activity.");
				finishVAST();
			} else {
				hideTitleStatusBars();
				mHandler = new Handler();
				DisplayMetrics displayMetrics = this.getResources()
						.getDisplayMetrics();

				mScreenWidth = displayMetrics.widthPixels;
				mScreenHeight = displayMetrics.heightPixels;
				mTrackingEventMap = mVastModel.getTrackingUrls();
				createUIComponents();

			}

		}

	}
	

	@Override
	protected void onStart() {
		VASTLog.d(TAG, "entered onStart --(life cycle event)");
		super.onStart();

	}

	@Override
	protected void onResume() {
		VASTLog.d(TAG, "entered on onResume --(life cycle event)");
		super.onResume();

	}
  
	@Override
	protected void onStop() {
		VASTLog.d(TAG, "entered on onStop --(life cycle event)");
		super.onStop();

	}

	@Override
	protected void onRestart() {
		VASTLog.d(TAG, "entered on onRestart --(life cycle event)");
		super.onRestart();
		createMediaPlayer();				

	}

	@Override
	protected void onPause() {
		VASTLog.d(TAG, "entered on onPause --(life cycle event)");
		super.onPause();
		
		if (mMediaPlayer != null) {
			mCurrentVideoPosition = mMediaPlayer.getCurrentPosition();
		}
		cleanActivityUp();
	}

	@Override
	protected void onDestroy() {
		VASTLog.d(TAG, "entered on onDestroy --(life cycle event)");
		super.onDestroy();

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		VASTLog.d(TAG, "entered onSaveInstanceState ");
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		VASTLog.d(TAG, "in onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);

	}

	private void hideTitleStatusBars() {
		// hide title bar of application
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// hide status bar of Android
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}
	
	private void createUIComponents() {

		LayoutParams params = new LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		this.createRootLayout(params);
		this.createSurface(params);
		this.createMediaPlayer();
		this.createOverlay(params);
		this.createButtonPanel(mScreenWidth, mScreenHeight);

		int size = Math.min(mScreenWidth, mScreenHeight);
		size = (int) (SKIP_INFO_SCALE * size);

		this.createPlayPauseButton(size);
		this.createCloseButton(size);

		this.createInfoButton(size);

		this.setContentView(mRootLayout);
		
		//this.createProgressDialog();
		this.createProgressBar();

	}

	private void createProgressBar() {
		LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);	
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		mProgressBar = new ProgressBar(this);
		mProgressBar.setLayoutParams(params);
		
		mRootLayout.addView(mProgressBar);
		mProgressBar.setVisibility(View.GONE);					
	}

	private void showProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);	
	}
	
	private void hideProgressBar() {
		mProgressBar.setVisibility(View.GONE);	
	}

	
	private void createRootLayout(LayoutParams params) {

		mRootLayout = new RelativeLayout(this);
		mRootLayout.setLayoutParams(params);
		mRootLayout.setPadding(0, 0, 0, 0);
		mRootLayout.setBackgroundColor(Color.BLACK);

	}

	@SuppressWarnings("deprecation")
	private void createSurface(LayoutParams params) {

		mSurfaceView = new SurfaceView(this);
		mSurfaceView.setLayoutParams(params);

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mRootLayout.addView(mSurfaceView);
	}

	private void createMediaPlayer() {

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnVideoSizeChangedListener(this);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

	}

	private void createOverlay(LayoutParams params) {

		mOverlay = new RelativeLayout(this);
		mOverlay.setLayoutParams(params);
		mOverlay.setPadding(0, 0, 0, 0);
		mOverlay.setBackgroundColor(Color.TRANSPARENT);
		mOverlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				overlayClicked();
			}
		});

		mRootLayout.addView(mOverlay);
	}

	private void createButtonPanel(int screenWidth, int screenHeight) {

		LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		mButtonPanel = new RelativeLayout(this);
		mButtonPanel.setLayoutParams(params);

		int padding = Math.min(screenWidth, screenHeight);
		padding = (int) (SKIP_INFO_PADDING_SCALE * padding);

		mButtonPanel.setPadding(padding, 0, padding, 0);
		mButtonPanel.setBackgroundColor(Color.BLACK);
		mButtonPanel.setVisibility(GONE);
		mOverlay.addView(mButtonPanel);
	}

	private void createInfoButton(int size) {

		String clickThroughUrl = mVastModel.getVideoClicks().getClickThrough();

		if (clickThroughUrl != null && clickThroughUrl.length() > 0) {

			LayoutParams params = new RelativeLayout.LayoutParams(size, size);
			params.addRule(RelativeLayout.LEFT_OF, R.id.nexage_vast_close_button);

			mInfoButton = new ImageButton(this);

			Drawable drawable = Assets.getDrawableFromBase64(getResources(), Assets.info);

			mInfoButton.setImageDrawable(drawable);		
			mInfoButton.setLayoutParams(params);
			mInfoButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
			mInfoButton.setPadding(0, 0, 0, 0);
			mInfoButton.setBackgroundColor(Color.TRANSPARENT);
			mInfoButton.setEnabled(true);
			mInfoButton.setVisibility(VISIBLE);
			mInfoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					infoClicked();
				}
			});

			mButtonPanel.addView(mInfoButton);

		}
	}

	private void createCloseButton(int size) {

		LayoutParams params = new RelativeLayout.LayoutParams(size, size);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		mCloseButton = new ImageButton(this);
		mCloseButton.setId(R.id.nexage_vast_close_button);

		Drawable drawable = Assets.getDrawableFromBase64(getResources(), Assets.exit);

		mCloseButton.setImageDrawable(drawable);
		mCloseButton.setLayoutParams(params);
		mCloseButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mCloseButton.setPadding(0, 0, 0, 0);
		mCloseButton.setBackgroundColor(Color.TRANSPARENT);
		mCloseButton.setVisibility(VISIBLE);
		mCloseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeClicked();
			}
		});

		mButtonPanel.addView(mCloseButton);
	}

	private void createPlayPauseButton(int size) {

		LayoutParams params = new RelativeLayout.LayoutParams(size, size);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

		mPauseDrawable = Assets.getDrawableFromBase64(getResources(), Assets.pause);
		mPlayDrawable = Assets.getDrawableFromBase64(getResources(), Assets.play);

		mPlayPauseButton = new ImageButton(this);
		mPlayPauseButton.setImageDrawable(mPauseDrawable);
		mPlayPauseButton.setLayoutParams(params);
		mPlayPauseButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mPlayPauseButton.setPadding(0, 0, 0, 0);
		mPlayPauseButton.setBackgroundColor(Color.TRANSPARENT);
		mPlayPauseButton.setEnabled(true);
		mPlayPauseButton.setVisibility(VISIBLE);
		mPlayPauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playPauseButtonClicked();
			}

		});

		mButtonPanel.addView(mPlayPauseButton);
	}

	private void infoClicked() {
		VASTLog.d(TAG, "entered infoClicked:");
		
		activateButtons(false);
		
		boolean isPlaying = mMediaPlayer.isPlaying();
	
		if (isPlaying) {
			mMediaPlayer.pause();
			mCurrentVideoPosition = mMediaPlayer.getCurrentPosition();
		}
				
		processClickThroughEvent();
				
	}

	private void activateButtons (boolean active) {
		VASTLog.d(TAG, "entered activateButtons:");
		
		if (active) {
			mButtonPanel.setVisibility(VISIBLE);	
		} else {
			mButtonPanel.setVisibility(GONE);	
		}
		
		
	}
	private void processClickThroughEvent() {
		VASTLog.d(TAG, "entered processClickThroughEvent:");
		
		if(VASTPlayer.listener!=null) {
			VASTPlayer.listener.vastClick();
		}
		
		String clickThroughUrl = mVastModel.getVideoClicks().getClickThrough();
		VASTLog.d(TAG, "clickThrough url: " + clickThroughUrl);

		
		// Before we send the app to the click through url, we will process ClickTracking URL's.
		List<String> urls = mVastModel.getVideoClicks().getClickTracking();
		fireUrls(urls);
		
		// Navigate to the click through url
		try {
			Uri uri = Uri.parse(clickThroughUrl);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			ResolveInfo resolvable = getPackageManager().resolveActivity(intent, PackageManager.GET_RESOLVED_FILTER);
			if(resolvable == null) {
				VASTLog.e(TAG, "Clickthrough error occured, uri unresolvable");
				if (mCurrentVideoPosition>=mMediaPlayer.getCurrentPosition()*0.99) {
					mMediaPlayer.start();
				}
				activateButtons(true);
				return;
			} else {
				startActivity(intent);
			}
		} catch (NullPointerException e) {
			VASTLog.e(TAG, e.getMessage(), e);
		}
	}

	private void closeClicked() {

		VASTLog.d(TAG, "entered closeClicked()");
		cleanActivityUp();
		
		if (!mIsPlayBackError) {
			this.processEvent(TRACKING_EVENTS_TYPE.close);
		}
		
		finishVAST();
		
		VASTLog.d(TAG, "leaving closeClicked()");
	}

	private void playPauseButtonClicked() {
		VASTLog.d(TAG, "entered playPauseClicked");
		if(mMediaPlayer==null) {
			VASTLog.e(TAG, "mMediaPlayer is null when playPauseButton was clicked");
			return;
		}
		boolean isPlaying = mMediaPlayer.isPlaying();
		VASTLog.d(TAG, "isPlaying:" + isPlaying);
		
		if (isPlaying) {
			//pause
			processPauseSteps();
			
		} else if(mIsVideoPaused) {
			//play
			this.processPlaySteps();		
			if(!mIsCompleted) {
				this.processEvent(TRACKING_EVENTS_TYPE.resume);
			}
		} else {
			//replay
			this.processPlaySteps();
			mQuartile = 0;
			this.startQuartileTimer();
		}
	}

	private void processPauseSteps() {
		mIsVideoPaused = true;
		mMediaPlayer.pause();
		this.stopVideoProgressTimer();
		this.stopToolBarTimer();
		mPlayPauseButton.setImageDrawable(mPlayDrawable);
		if(!mIsCompleted) {
			this.processEvent(TRACKING_EVENTS_TYPE.pause);
		}
	}


	private void processPlaySteps() {
		mIsVideoPaused = false;
		mMediaPlayer.start();
		mPlayPauseButton.setImageDrawable(mPauseDrawable);
		this.startToolBarTimer();
		this.startVideoProgressTimer();
	}
	
	
	@Override
	public void onBackPressed() {
		VASTLog.d(TAG, "entered onBackPressed");
		this.closeClicked();

	}

	public void surfaceCreated(SurfaceHolder holder) {
		VASTLog.d(TAG, "surfaceCreated -- (SurfaceHolder callback)");
		try {
			if(mMediaPlayer==null) {
				createMediaPlayer();
			}
			this.showProgressBar();
			mMediaPlayer.setDisplay(mSurfaceHolder);
			String url = mVastModel.getPickedMediaFileURL();

			VASTLog.d(TAG, "URL for media file:" + url);
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepareAsync();
		} catch (Exception e) {
			VASTLog.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int arg1, int arg2,
			int arg3) {
		VASTLog.d(TAG,
				"entered surfaceChanged -- (SurfaceHolder callback)");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		VASTLog
				.d(TAG,
						"entered surfaceDestroyed -- (SurfaceHolder callback)");
		cleanUpMediaPlayer();

	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		VASTLog
				.d(TAG,
						"entered onVideoSizeChanged -- (MediaPlayer callback)");
		mVideoWidth = width;
		mVideoHeight = height;
		VASTLog.d(TAG, "video size: " + mVideoWidth + "x" + mVideoHeight);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		VASTLog
				.d(TAG,
						"entered onPrepared called --(MediaPlayer callback) ....about to play");

		calculateAspectRatio();

		mMediaPlayer.start();
		
		this.hideProgressBar();

		if (mIsVideoPaused) {
			VASTLog.d(TAG, "pausing video");
			mMediaPlayer.pause();
		} else {
			this.startVideoProgressTimer();
		}

		VASTLog.d(TAG, "current location in video:"
				+ mCurrentVideoPosition);
		if (mCurrentVideoPosition > 0) {
			VASTLog.d(TAG, "seeking to location:"
					+ mCurrentVideoPosition);
			mMediaPlayer.seekTo(mCurrentVideoPosition);

		}

		if (!mIsProcessedImpressions) {
			this.processImpressions();			
		}
		
		startQuartileTimer();
		startToolBarTimer();
		
		if(!mMediaPlayer.isPlaying() && !mIsVideoPaused) {
			mMediaPlayer.start();
		}
	}


	private void calculateAspectRatio() {
		VASTLog.d(TAG, "entered calculateAspectRatio");
		
		if ( mVideoWidth == 0 || mVideoHeight == 0 ) {
			VASTLog.w(TAG, "mVideoWidth or mVideoHeight is 0, skipping calculateAspectRatio");
			return;
		}
		
		VASTLog.d(TAG, "calculating aspect ratio");
		double widthRatio = 1.0 * mScreenWidth / mVideoWidth;
		double heightRatio = 1.0 * mScreenHeight / mVideoHeight;

		double scale = Math.min(widthRatio, heightRatio);

		int surfaceWidth = (int) (scale * mVideoWidth);
		int surfaceHeight = (int) (scale * mVideoHeight);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				surfaceWidth, surfaceHeight);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		mSurfaceView.setLayoutParams(params);

		mSurfaceHolder.setFixedSize(surfaceWidth, surfaceHeight);

		VASTLog
				.d(TAG, " screen size: " + mScreenWidth + "x" + mScreenHeight);
		VASTLog.d(TAG, " video size:  " + mVideoWidth + "x" + mVideoHeight);
		VASTLog.d(TAG, " widthRatio:   " + widthRatio);
		VASTLog.d(TAG, " heightRatio:   " + heightRatio);

		VASTLog
				.d(TAG, "surface size: " + surfaceWidth + "x" + surfaceHeight);

	}

	private void cleanActivityUp() {

		this.cleanUpMediaPlayer();
		this.stopQuartileTimer();
		this.stopVideoProgressTimer();
		this.stopToolBarTimer();
	}

	private void cleanUpMediaPlayer() {

		VASTLog.d(TAG, "entered cleanUpMediaPlayer ");

		if (mMediaPlayer != null) {

			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}

			mMediaPlayer.setOnCompletionListener(null);
			mMediaPlayer.setOnErrorListener(null);
			mMediaPlayer.setOnPreparedListener(null);
			mMediaPlayer.setOnVideoSizeChangedListener(null);

			mMediaPlayer.release();
			mMediaPlayer = null;
		}

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		VASTLog.e(TAG, "entered onError -- (MediaPlayer callback)");
		mIsPlayBackError = true;
		VASTLog.e(TAG, "Shutting down Activity due to Media Player errors: WHAT:" + what +": EXTRA:" + extra+":");

		processErrorEvent();
		this.closeClicked();

		return true;
	}

	private void processErrorEvent() {
		VASTLog.d(TAG, "entered processErrorEvent");
	
		List<String> errorUrls = mVastModel.getErrorUrl();		
		fireUrls(errorUrls);
		
	}
	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		VASTLog
				.d(TAG, "entered onCOMPLETION -- (MediaPlayer callback)");
		stopVideoProgressTimer();
		stopToolBarTimer();
		mButtonPanel.setVisibility(VISIBLE);
		mPlayPauseButton.setImageDrawable(mPlayDrawable);		
		if ( !mIsPlayBackError && !mIsCompleted) {
			mIsCompleted = true;
			this.processEvent(TRACKING_EVENTS_TYPE.complete);
			
			if(VASTPlayer.listener!=null) {
				VASTPlayer.listener.vastComplete();
			}
		}

	}

	private void overlayClicked() {
		this.startToolBarTimer();
	}

	private void processImpressions() {
		VASTLog.d(TAG, "entered processImpressions");
		
		mIsProcessedImpressions = true;
		List<String> impressions = mVastModel.getImpressions();
		fireUrls(impressions);
		
	}
	
	private void fireUrls(List<String> urls) {
		VASTLog.d(TAG, "entered fireUrls");
		
		if (urls != null) {
			
			for (String url : urls) {
				VASTLog.v(TAG, "\tfiring url:" + url);
				HttpTools.httpGetURL(url);
			}
			
		}else {
			VASTLog.d(TAG, "\turl list is null");
		}
			
		

	}

	// Timers

	private void startToolBarTimer() {
		VASTLog.d(TAG, "entered startToolBarTimer");
		if(mQuartile==4) {
			// we are at the end of the video, we dont want ot ever hide the toolbar now
			return;
		}
		if (mMediaPlayer!= null && mMediaPlayer.isPlaying()) {   
			stopToolBarTimer();
			mToolBarTimer = new Timer();
			mToolBarTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						public void run() {
							VASTLog.d(TAG, "hiding buttons");
							mButtonPanel.setVisibility(GONE);
						}
					});
				}
			}, TOOLBAR_HIDE_DELAY);

			mButtonPanel.setVisibility(VISIBLE);
		}
		
		if (mIsVideoPaused) {
			activateButtons(true);
		}
	}

	private void stopToolBarTimer() {
		VASTLog.d(TAG, "entered stopToolBarTimer");
		if (mToolBarTimer != null) {
			mToolBarTimer.cancel();
			mToolBarTimer = null;
		}
	}

	private void startQuartileTimer() {
		VASTLog.d(TAG, "entered startQuartileTimer");
		stopQuartileTimer();

		if(mIsCompleted) {
			VASTLog.d(TAG, "ending quartileTimer becuase the video has been replayed");
			return;
		}
		
		final int videoDuration = mMediaPlayer.getDuration();

		mTrackingEventTimer = new Timer();
		mTrackingEventTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				int percentage = 0;
				try {
					int curPos = mMediaPlayer.getCurrentPosition();
					// wait for the video to really start
					if (curPos == 0) {
						return;
					}
					percentage = 100 * curPos / videoDuration;
				} catch (Exception e) {
					VASTLog.w(
							TAG,
							"mediaPlayer.getCurrentPosition exception: "
									+ e.getMessage());
					this.cancel();
					return;
				}

				if (percentage >= 25 * mQuartile) {
					if (mQuartile == 0) {
						VASTLog.i(TAG, "Video at start: (" + percentage
								+ "%)");
						processEvent(TRACKING_EVENTS_TYPE.start);
					} else if (mQuartile == 1) {
						VASTLog.i(TAG, "Video at first quartile: ("
								+ percentage + "%)");
						processEvent(TRACKING_EVENTS_TYPE.firstQuartile);
					} else if (mQuartile == 2) {
						VASTLog.i(TAG, "Video at midpoint: ("
								+ percentage + "%)");
						processEvent(TRACKING_EVENTS_TYPE.midpoint);
					} else if (mQuartile == 3) {
						VASTLog.i(TAG, "Video at third quartile: ("
								+ percentage + "%)");
						processEvent(TRACKING_EVENTS_TYPE.thirdQuartile);
						stopQuartileTimer();
					}
					mQuartile++;
				}
			}

		}, 0, QUARTILE_TIMER_INTERVAL);
	}

	private void stopQuartileTimer() {

		if (mTrackingEventTimer != null) {
			mTrackingEventTimer.cancel();
			mTrackingEventTimer = null;
		}
	}

	private void startVideoProgressTimer() {
		VASTLog.d(TAG, "entered startVideoProgressTimer");

		mStartVideoProgressTimer = new Timer();
		mVideoProgressTracker = new LinkedList<Integer>();

		mStartVideoProgressTimer.schedule(new TimerTask() {
			int maxAmountInList = mMaxProgressTrackingPoints - 1;

			@Override
			public void run() {

				if (mMediaPlayer == null) {
					return;
				}

				if (mVideoProgressTracker.size() == maxAmountInList) {
					int firstPosition = mVideoProgressTracker.getFirst();
					int lastPosition = mVideoProgressTracker.getLast();

					if (lastPosition > firstPosition) {
						VASTLog.v(TAG, "video progressing (position:"+lastPosition+")");
						mVideoProgressTracker.removeFirst();
					} else {
						VASTLog.e(TAG, "detected video hang");
						mIsPlayBackError = true;
						stopVideoProgressTimer();
						processErrorEvent();
						closeClicked();
						finishVAST();
					}
				}

				try {
					int curPos = mMediaPlayer.getCurrentPosition();
					mVideoProgressTracker.addLast(curPos);
				} catch (Exception e) {
					// occasionally the timer is in the middle of processing and
					// the media player is cleaned up
				}
			}

		}, 0, VIDEO_PROGRESS_TIMER_INTERVAL);

	}

	private void stopVideoProgressTimer() {
		VASTLog.d(TAG, "entered stopVideoProgressTimer");

		if (mStartVideoProgressTimer != null) {

			mStartVideoProgressTimer.cancel();
		}
	}

	private void processEvent(TRACKING_EVENTS_TYPE eventName) {
		VASTLog.i(TAG, "entered Processing Event: " + eventName);
		List<String> urls = (List<String>) mTrackingEventMap.get(eventName);

		fireUrls(urls);
		
	}

	private void finishVAST() {
		VASTPlayer.listener.vastDismiss();
		finish();
	}
}
