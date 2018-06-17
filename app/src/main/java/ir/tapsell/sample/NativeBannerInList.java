package ir.tapsell.sample;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
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

import ir.tapsell.sdk.nativeads.TapsellNativeBannerAd;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerAdLoadListener;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerAdLoader;

public class NativeBannerInList extends AppCompatActivity {


    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_banner_in_list);

        RecyclerView rvItems = findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(NativeBannerInList.this, LinearLayoutManager.VERTICAL, false));
        rvItems.setAdapter(new MyAdapter(NativeBannerInList.this));
    }

    public class TapsellListItemHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        TapsellListItemHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        public void bindView(int index) {
            tvTitle.setText("Item " + index);
        }

    }

    public class TapsellListItemAdHolder extends RecyclerView.ViewHolder {
        FrameLayout adContainer;
        Context mContext;

        TapsellListItemAdHolder(View itemView, Context context) {
            super(itemView);
            adContainer = itemView.findViewById(R.id.adContainer);
            mContext = context;
        }

        public void bindView(TapsellNativeBannerAd adView) {
            adContainer.removeAllViews();
            adView.addToParentView(adContainer);
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

        private final Map<Integer, TapsellNativeBannerAd> ads = new HashMap<>();

        MyAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_AD) {
                return new TapsellListItemAdHolder(mInflater.inflate(R.layout.list_ad_item, parent, false), mContext);
            } else {
                return new TapsellListItemHolder(mInflater.inflate(R.layout.list_item, parent, false));
            }
        }

        @Override
        public int getItemViewType(int position) {
            // Just as an example, return 0 or 2 depending on position
            // Note that unlike in ListView adapters, types don't have to be contiguous
            return ((position + 1) % 10) == 0 ? VIEW_TYPE_AD : VIEW_TYPE_ITEM;
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == VIEW_TYPE_AD) {
                if (ads.containsKey(position)) {
                    ((TapsellListItemAdHolder) holder).bindView(ads.get(position));
                } else {
                    ((TapsellListItemAdHolder) holder).clear();
                    new TapsellNativeBannerAdLoader.Builder()
                            .setContentViewTemplate(R.layout.tapsell_small_content_banner_ad_template)
                            .loadAd(NativeBannerInList.this, BuildConfig.tapsellNativeBannerZoneId, new TapsellNativeBannerAdLoadListener() {

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
                                public void onRequestFilled(TapsellNativeBannerAd ad) {
                                    Log.e("Tapsell", "Native Banner AdView Available");
//                                    adView.setBackgroundColor(Color.rgb(255,255,220));
                                    ads.put(holder.getAdapterPosition(), ad);
                                    ((TapsellListItemAdHolder) holder).bindView(ad);
                                    (ad.findViewById(R.id.tapsell_nativead_description)).setSelected(true);
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(NativeBannerInList.this, "onRequestFilled", Toast.LENGTH_LONG).show();
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
    }

}
