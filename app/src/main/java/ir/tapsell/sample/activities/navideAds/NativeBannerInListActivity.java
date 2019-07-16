package ir.tapsell.sample.activities.navideAds;

import android.os.Bundle;
import android.util.Log;

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
import ir.tapsell.sdk.nativeads.TapsellNativeBannerManager;

public class NativeBannerInListActivity extends AppCompatActivity {

    private final static String TAG = "NativeBanner";
    private final int PAGE_SIZE = 20;
    private int currentPage = 0;
    private boolean isLoading = false;

    private NativeBannerAdapter adapter;

    private RecyclerView rvItems;

    private ArrayList<ItemList> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_banner_in_list);

        initView();
    }

    private void initView() {
        rvItems = findViewById(R.id.rvItems);

        initList();
        getNativeBannerAd();
        generateItems(0);
    }

    private void initList() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvItems.setLayoutManager(layoutManager);

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

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        isLoading = true;
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

        adapter.updateItem(items);
        getNativeBannerAd();
    }

    private void getNativeBannerAd() {
        TapsellNativeBannerManager.getAd(this, BuildConfig.TAPSELL_NATIVE_BANNER,
                new AdRequestCallback() {
                    @Override
                    public void onResponse(String[] adIds) {
                        showAd(adIds);
                        Log.d(TAG, "get ad success");
                    }

                    @Override
                    public void onFailed(String s) {
                        Log.d(TAG, "get ad failed");
                    }
                });
    }

    private void showAd(String[] adIds) {
        ItemList item = new ItemList();
        item.id = adIds[0];
        item.listItemType = ListItemType.AD;
        items.add(item);
        adapter.updateItem(items);
    }
}
