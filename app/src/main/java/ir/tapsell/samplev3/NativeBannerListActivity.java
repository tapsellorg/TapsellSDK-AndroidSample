package ir.tapsell.samplev3;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
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

import ir.tapsell.samplev3.utils.TapsellConstants;
import ir.tapsell.sdk.nativeads.android.TapsellNativeBannerAdLoadListener;
import ir.tapsell.sdk.nativeads.android.TapsellNativeBannerAdLoader;

public class NativeBannerListActivity extends Activity {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_banner_list);

        RecyclerView rvItems = (RecyclerView) findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(NativeBannerListActivity.this, LinearLayoutManager.VERTICAL, false));
        rvItems.setAdapter(new MyAdapter(NativeBannerListActivity.this));
    }

    private class TapsellListItemHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitle;
        TapsellListItemHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }

        void bindView(int index)
        {
            tvTitle.setText("Item "+index);
        }

    }

    private class TapsellListItemAdHolder extends RecyclerView.ViewHolder
    {
        FrameLayout adContainer;
        Context mContext;
        TapsellListItemAdHolder(View itemView, Context context) {
            super(itemView);
            adContainer = (FrameLayout) itemView.findViewById(R.id.adContainer);
            mContext = context;
        }

        void bindView(View adView)
        {
            if(adView.getParent()!=null)
            {
                ((ViewGroup)adView.getParent()).removeView(adView);
            }
            adContainer.removeAllViews();
            adContainer.addView(adView);
        }

    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private final Context mContext;
        private final LayoutInflater mInflater;

        private static final int VIEW_TYPE_ITEM=0;
        private static final int VIEW_TYPE_AD=1;

        private final Map<Integer, View> ads = new HashMap<>();

        MyAdapter(Context context)
        {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==VIEW_TYPE_AD)
            {
                return new TapsellListItemAdHolder(mInflater.inflate(R.layout.list_ad_item,parent,false),mContext);
            }
            else
            {
                return new TapsellListItemHolder(mInflater.inflate(R.layout.list_item,parent,false));
            }
        }

        @Override
        public int getItemViewType(int position) {
            // Just as an example, return 0 or 2 depending on position
            // Note that unlike in ListView adapters, types don't have to be contiguous
            return ((position+1) % 10)==0? VIEW_TYPE_AD : VIEW_TYPE_ITEM ;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if(getItemViewType(position)==VIEW_TYPE_AD)
            {
                if(ads.containsKey(position))
                {
                    ((TapsellListItemAdHolder)holder).bindView(ads.get(position));
                }
                else
                {
                    new TapsellNativeBannerAdLoader.Builder()
                            .setContentViewTemplate(R.layout.tapsell_small_content_banner_ad_template)
                            .loadAd(NativeBannerListActivity.this, TapsellConstants.nativeBannerListZoneId, ((TapsellListItemAdHolder)holder).adContainer, new TapsellNativeBannerAdLoadListener() {

                                @Override
                                public void onNoNetwork() {
                                    Log.e("Tapsell","No Network Available");
                                }

                                @Override
                                public void onNoAdAvailable() {
                                    Log.e("Tapsell","No Native Banner Ad Available!");
                                }

                                @Override
                                public void onError(final String error) {
                                    Log.e("Tapsell","Error: "+error);
                                }

                                @Override
                                public void onRequestFilled(View adView, ViewGroup parentView) {
                                    Log.e("Tapsell","Native Banner AdView Available");
                                    adView.setBackgroundColor(Color.rgb(255,255,220));
                                    ads.put(holder.getAdapterPosition(),adView);
                                    ((TapsellListItemAdHolder)holder).bindView(adView);
                                    (adView.findViewById(R.id.tapsell_nativead_description)).setSelected(true);
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(NativeBannerListActivity.this,"onRequestFilled",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                }
            }
            else
            {
                ((TapsellListItemHolder)holder).bindView(position);
            }

        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }

}
