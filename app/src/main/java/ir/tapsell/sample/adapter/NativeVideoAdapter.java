package ir.tapsell.sample.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAd;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdCompletionListener;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoadListener;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoader;

public class NativeVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_AD = 1;
    private final static String TAG = "NativeVideoAdapter";
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final Map<Integer, TapsellNativeVideoAd> ads = new HashMap<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public NativeVideoAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_AD) {
            return new TapsellListItemAdHolder(mInflater.inflate(R.layout.list_large_ad_item,
                    parent, false), mContext);
        } else {
            return new TapsellListItemHolder(mInflater.inflate(R.layout.list_large_item,
                    parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ((position + 1) % 10) == 0 ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final TapsellListItemAdHolder tapsellListItemHolder = (TapsellListItemAdHolder) holder;

        if (getItemViewType(position) == VIEW_TYPE_AD) {
            if (ads.containsKey(position)) {
                tapsellListItemHolder.bindView(ads.get(position));
            } else {
                tapsellListItemHolder.clear();
                new TapsellNativeVideoAdLoader.Builder()
                        .setContentViewTemplate(R.layout.tapsell_content_video_ad_template)
                        .setAppInstallationViewTemplate(R.layout.tapsell_app_installation_video_ad_template)
                        .setAutoStartVideoOnScreenEnabled(true)
                        .setFullscreenBtnEnabled(false)
                        .loadAd(mContext, BuildConfig.TAPSELL_NATIVE_VIDEO,
                                new TapsellNativeVideoAdLoadListener() {
                                    @Override
                                    public void onNoNetwork() {
                                        Log.e(TAG, "No Network Available");
                                    }

                                    @Override
                                    public void onNoAdAvailable() {
                                        Log.e(TAG, "No Native Banner Ad Available!");
                                    }

                                    @Override
                                    public void onError(final String error) {
                                        Log.e(TAG, "Error: " + error);
                                    }

                                    @Override
                                    public void onRequestFilled(
                                            TapsellNativeVideoAd tapsellNativeVideoAd
                                    ) {
                                        Log.e(TAG, "Native Banner AdView Available");

                                        ads.put(holder.getAdapterPosition(), tapsellNativeVideoAd);
                                        tapsellListItemHolder.bindView(tapsellNativeVideoAd);

                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, "onRequestFilled",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                });
            }
        } else {
            ((TapsellListItemHolder) holder).bindView(position);
        }
    }

    @Override
    public int getItemCount() {
        return 50;
    }

    class TapsellListItemHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        TapsellListItemHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        void bindView(int index) {
            tvTitle.setText(mContext.getString(R.string.item, String.valueOf(index)));
        }

    }

    class TapsellListItemAdHolder extends RecyclerView.ViewHolder {
        FrameLayout adContainer;
        Context mContext;

        TapsellListItemAdHolder(View itemView, Context context) {
            super(itemView);
            adContainer = itemView.findViewById(R.id.adContainer);
            mContext = context;
        }

        void bindView(TapsellNativeVideoAd ad) {
            adContainer.removeAllViews();
            ad.setCompletionListener(new TapsellNativeVideoAdCompletionListener() {
                @Override
                public void onAdShowFinished(String adId) {
                    Log.e(TAG, "onAdShowFinished: " + adId);
                }
            });
            ad.addToParentView(adContainer);
        }

        void clear() {
            adContainer.removeAllViews();
        }
    }
}