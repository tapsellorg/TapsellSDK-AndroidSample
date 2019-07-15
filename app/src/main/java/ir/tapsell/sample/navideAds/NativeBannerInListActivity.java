package ir.tapsell.sample.navideAds;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ir.tapsell.sample.BuildConfig;
import ir.tapsell.sample.R;
import ir.tapsell.sample.adapter.NativeBannerAdapter;
import ir.tapsell.sample.enums.ListItemType;
import ir.tapsell.sample.model.ItemList;
import ir.tapsell.sdk.AdRequestCallback;
import ir.tapsell.sdk.CacheSize;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerManager;

public class NativeBannerInListActivity extends AppCompatActivity {

    private final static String TAG = "NativeBanner";
    private final String STATE_LIST = "STATE_LIST";
    private final int PAGE_SIZE = 20;
    private int currentPage = 0;
    private boolean isLoading = false;

    private NativeBannerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private RecyclerView rvItems;
    private Button btnNativeBanner, btnShow;

    private ArrayList<ItemList> items = new ArrayList<>();
    private String[] adId = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_LIST, items);

        TapsellNativeBannerManager.onSaveInstanceState(this,
                BuildConfig.TAPSELL_NATIVE_BANNER, outState);

        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {
        TapsellNativeBannerManager.onRestoreInstanceState(this,
                BuildConfig.TAPSELL_NATIVE_BANNER, savedInstanceState);

        items.addAll((ArrayList<ItemList>) savedInstanceState.getSerializable(STATE_LIST));
        updateList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_banner_in_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView(savedInstanceState);
    }

    private void initView(final Bundle savedInstanceState) {
        btnNativeBanner = findViewById(R.id.btnNativeBanner);
        btnShow = findViewById(R.id.btnShow);
        btnShow.setEnabled(false);

        btnNativeBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAdCache();
                initList();

                if (savedInstanceState != null) {
                    restoreState(savedInstanceState);
                    return;
                }
                generateItems(0);
            }
        });
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAd();
            }
        });
    }

    private void initAdCache() {
        TapsellNativeBannerManager.createCache(this,
                BuildConfig.TAPSELL_NATIVE_BANNER, CacheSize.MEDIUM);
    }

    private void initList() {
        rvItems = findViewById(R.id.rvItems);
        linearLayoutManager = new LinearLayoutManager(this);
        rvItems.setLayoutManager(linearLayoutManager);

        adapter = new NativeBannerAdapter(this);
        rvItems.setAdapter(adapter);

        rvItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy < 0) {
                    return;
                }

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        generateItems(++currentPage);
                    }
                }
            }
        });
    }

    private void generateItems(int page) {
        for (int i = 0; i < PAGE_SIZE; i++) {
            ItemList item = new ItemList();
            item.title = "item " + (page * PAGE_SIZE + i);
            item.listItemType = ListItemType.ITEM;
            items.add(item);
        }

        isLoading = false;

        updateList();
        getNativeBannerAd();
    }

    private void getNativeBannerAd() {
        TapsellNativeBannerManager.getAd(this, BuildConfig.TAPSELL_NATIVE_BANNER,
                new AdRequestCallback() {
                    @Override
                    public void onResponse(String[] strings) {
                        adId = strings;
                        Log.d(TAG, "get ad success");
                        btnShow.setEnabled(true);
                    }

                    @Override
                    public void onFailed(String s) {
                        Log.d(TAG, "get ad failed");
                    }
                });
    }

    private void updateList() {
        rvItems.post(new Runnable() {
            @Override
            public void run() {
                adapter.updateItem(items);
            }
        });
    }

    private void showAd() {
        if (adId != null) {
            ItemList item = new ItemList();
            item.id = adId[0];
            item.listItemType = ListItemType.AD;
            items.add(item);
            updateList();
        }
        btnShow.setEnabled(false);
    }
}
