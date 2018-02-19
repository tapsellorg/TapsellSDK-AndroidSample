package ir.tapsell.sample;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import ir.tapsell.sdk.nativeads.TapsellNativeVideoAd;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdCompletionListener;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoadListener;
import ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoader;

public class NativeVideoInList extends AppCompatActivity {

    private final Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_video_in_list);

        RecyclerView rvItems = (RecyclerView) findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(NativeVideoInList.this, LinearLayoutManager.VERTICAL, false));
        rvItems.setAdapter(new NativeVideoInList.MyAdapter(NativeVideoInList.this));
    }

    public class TapsellListItemHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public TapsellListItemHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }

        public void bindView(int index) {
            tvTitle.setText("Item " + index);
        }

    }

    public class TapsellListItemAdHolder extends RecyclerView.ViewHolder {
        FrameLayout adContainer;
        Context mContext;

        public TapsellListItemAdHolder(View itemView, Context context) {
            super(itemView);
            adContainer = (FrameLayout) itemView.findViewById(R.id.adContainer);
            mContext = context;
        }

        public void bindView(TapsellNativeVideoAd ad) {
            adContainer.removeAllViews();
            ad.setCompletionListener(new TapsellNativeVideoAdCompletionListener() {
                @Override
                public void onAdShowFinished(String adId) {
                    Log.e("Tapsell","onAdShowFinished: "+adId);
                }
            });
            ad.addToParentView(adContainer);
        }

        public void clear() {
            adContainer.removeAllViews();
        }

    }

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final Context mContext;
        private final LayoutInflater mInflater;

        private static final int VIEW_TYPE_ITEM = 0;
        private static final int VIEW_TYPE_AD = 1;

        private final Map<Integer, TapsellNativeVideoAd> ads = new HashMap<>();

        public MyAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_AD) {
                return new NativeVideoInList.TapsellListItemAdHolder(mInflater.inflate(R.layout.list_large_ad_item, parent, false), mContext);
            } else {
                return new NativeVideoInList.TapsellListItemHolder(mInflater.inflate(R.layout.list_large_item, parent, false));
            }
        }

        @Override
        public int getItemViewType(int position) {
            // Just as an example, return 0 or 2 depending on position
            // Note that unlike in ListView adapters, types don't have to be contiguous
            return ((position + 1) % 10) == 0 ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == VIEW_TYPE_AD) {
                if (ads.containsKey(position)) {
                    ((NativeVideoInList.TapsellListItemAdHolder) holder).bindView(ads.get(position));
                } else {
                    ((NativeVideoInList.TapsellListItemAdHolder) holder).clear();
                    new TapsellNativeVideoAdLoader.Builder()
                            .setContentViewTemplate(R.layout.tapsell_content_video_ad_template)
                            .setAppInstallationViewTemplate(R.layout.tapsell_app_installation_video_ad_template)
                            .setAutoStartVideoOnScreenEnabled(true)
                            .setFullscreenBtnEnabled(false)
                            .loadAd(NativeVideoInList.this, G.nativeVideoZoneId,
                                    new TapsellNativeVideoAdLoadListener() {

                                @Override
                                public void onNoNetwork() {
                                    Log.e("Tapsell", "No Network Available");
                                }

                                @Override
                                public void onNoAdAvailable() {
                                    Log.e("Tapsell", "No Native Banner Ad Available!");
                                }

                                @Override
                                public void onError(final String error) {
                                    Log.e("Tapsell", "Error: " + error);
                                }

                                @Override
                                public void onRequestFilled(TapsellNativeVideoAd tapsellNativeVideoAd) {
                                    Log.e("Tapsell", "Native Banner AdView Available");
                                    ads.put(holder.getAdapterPosition(), tapsellNativeVideoAd);
                                    ((NativeVideoInList.TapsellListItemAdHolder) holder).bindView(tapsellNativeVideoAd);
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(NativeVideoInList.this, "onRequestFilled", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            });
                }
            } else {
                ((NativeVideoInList.TapsellListItemHolder) holder).bindView(position);
            }

        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }

}
