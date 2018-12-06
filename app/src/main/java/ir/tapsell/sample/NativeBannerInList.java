package ir.tapsell.sample;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.tapsell.sample.adapter.NativeBannerAdapter;
import ir.tapsell.sample.model.ItemList;
import ir.tapsell.sample.type.ListItemType;
import ir.tapsell.sdk.AdRequestCallback;
import ir.tapsell.sdk.CacheSize;
import ir.tapsell.sdk.nativeads.TapsellNativeBannerManager;

public class NativeBannerInList extends AppCompatActivity {

    private final String STATE_LIST = "STATE_LIST";

    private final int PAGE_SIZE = 20;

    private ArrayList<ItemList> items = new ArrayList<>();

    private NativeBannerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private boolean isLoading = false;
    private int currentPage = 0;

    private RecyclerView rvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_banner_in_list);

        initAdCache();
        initList();

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
            return;
        }

        generateItems(0);
    }

    private void restoreState(Bundle savedInstanceState) {
        TapsellNativeBannerManager.onRestoreInstanceState(this,
                BuildConfig.tapsellNativeBannerZoneId, savedInstanceState);

        items.addAll((ArrayList<ItemList>) savedInstanceState.getSerializable(STATE_LIST));

        updateList();
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
        getTapsellAd();
    }

    private void initAdCache() {
        TapsellNativeBannerManager.createCache(this,
                BuildConfig.tapsellNativeBannerZoneId, CacheSize.MEDIUM);
    }

    private void getTapsellAd() {
        TapsellNativeBannerManager.getAd(this, BuildConfig.tapsellNativeBannerZoneId,
                new AdRequestCallback() {
                    @Override
                    public void onResponse(String[] strings) {
                        onAdResponse(strings);

                    }

                    @Override
                    public void onFailed(String s) {
                        Log.e(getClass().getName(), "get ad fail");

                    }
                });
    }

    private void onAdResponse(String[] adsId) {
        ItemList item = new ItemList();

        item.id = adsId[0];
        item.listItemType = ListItemType.AD;

        items.add(item);

        updateList();
    }

    private void updateList() {
        rvItems.post(new Runnable() {
            @Override
            public void run() {
                adapter.updateItem(items);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_LIST, items);

        TapsellNativeBannerManager.onSaveInstanceState(this,
                BuildConfig.tapsellNativeBannerZoneId, outState);

        super.onSaveInstanceState(outState);
    }
}
