package com.sysu.sjk.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.BundleCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sysu.sjk.base.BaseActivity;
import com.sysu.sjk.bean.ApiResponse;
import com.sysu.sjk.bean.Gank;
import com.sysu.sjk.constant.Constants;
import com.sysu.sjk.network.ApiClient;
import com.sysu.sjk.utils.ConnectUtils;
import com.sysu.sjk.utils.FileUtils;
import com.sysu.sjk.utils.Logger;
import com.sysu.sjk.utils.ThreadUtils;
import com.sysu.sjk.view.adapter.GankListAdapter;
import com.sysu.sjk.view.tmp_interface.MyClickListener;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by sjk on 16-10-21.
 */
public class FirstActivity extends BaseActivity implements MyClickListener {

    LinearLayout emptyLayout;
    SwipeRefreshLayout gankSwipeRefreshLayout;
    RecyclerView gankRecyclerView;
    LinearLayoutManager gankLinearLayoutManager;
    RecyclerView.ItemDecoration gankItemDecoration = null;
    RecyclerView.ItemAnimator gankItemAnimator = null;
    GankListAdapter gankAdapter;
    List<Gank> gankList= new ArrayList<>();

    Subscription gankListSubscription;

    private int lastVisibleItem = 0;
    private int pageIndex = 1;  // note that index is from 1, not 0.(indicated by given API)
    private final int PAGE_SIZE = 10;
    private boolean isLoadMore = false; // to record whether it's 'refresh' or 'load more'

    @Override
    public int getLayoutId() {
        return R.layout.activity_first;
    }

    @Override
    public void initView() {
        emptyLayout = (LinearLayout)findViewById(R.id.gank_empty_list);
        gankRecyclerView = (RecyclerView)findViewById(R.id.gank_recycler_view);
        gankSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.gank_swipe_refresh_layout);
        configRecyclerViewAndSwipeRefreshLayout();
    }

    @Override
    public void initData() {
        new GetHasReadTask(this)
                .executeOnExecutor(ThreadUtils.getWorkerThreads());

        gankAdapter = new GankListAdapter(FirstActivity.this, gankList);
        gankRecyclerView.setAdapter(gankAdapter);

        // auto request when entering the activity
        pageIndex = 1;
        isLoadMore = false;
        requestData();
        // the refreshing icon automatically appear
        setSwipeLoading(true);
    }

    @Override
    public void initListener() {
        gankAdapter.setMyClickListener(this);
        gankSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                isLoadMore = false;
                requestData();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.gank_list_toolbar);
        setSupportActionBar(toolbar);
        // Immitate the double click, to go back top of the list.
        toolbar.setOnTouchListener(new View.OnTouchListener() {

            private final int DOUBLE_CLICK_INTERVAL = 300;
            private long lastTapTime = 0;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        long thisTapTime = System.currentTimeMillis();
                        if (thisTapTime - lastTapTime < DOUBLE_CLICK_INTERVAL) {
                            goBackTopOfList();
                        } else {
                            lastTapTime = thisTapTime;
                        }
                        break;
                }

                return true;
            }
        });
    }

    private void configRecyclerViewAndSwipeRefreshLayout() {
        gankLinearLayoutManager = new LinearLayoutManager(FirstActivity.this);
        gankLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gankRecyclerView.setLayoutManager(gankLinearLayoutManager);

        gankItemAnimator = new DefaultItemAnimator();
        gankRecyclerView.setItemAnimator(gankItemAnimator);

        gankItemDecoration = null;
        //gankRecyclerView.addItemDecoration(gankItemDecoration);

        // load more
        gankRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == gankList.size()) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = gankLinearLayoutManager.findLastVisibleItemPosition();
            }
        });

        gankSwipeRefreshLayout.setColorSchemeColors(
                /*Color.RED,
                Color.GREEN,
                Color.BLUE,
                Color.YELLOW,*/
                Color.CYAN
        );
    }

    // network request
    // specifically, get the page of {pageIndex}
    private void requestData() {
        Logger.log("request");

        // firstly check the network availability
        if (!ConnectUtils.isNetworkConnected(FirstActivity.this)) {
            Snackbar.make(gankRecyclerView, "Bad network!", Snackbar.LENGTH_LONG)
                    .show();
            hideRefreshIcon();
            return;
        }

        // then execute the network request
        gankListSubscription = ApiClient.getService().getAndroidGanksByPage(PAGE_SIZE, pageIndex)
                .map(new Func1<ApiResponse<Gank>, List<Gank>>() {
                    @Override
                    public List<Gank> call(ApiResponse<Gank> gankApiResponse) {
                        return gankApiResponse.getResults();
                    }
                })
                .subscribeOn(Schedulers.io())               // request data on IO thread
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())  // control UI on main(UI) thread
                .subscribe(new Subscriber<List<Gank>>() {
                    @Override
                    public void onCompleted() {
                        Logger.log("complete~");
                        hideRefreshIcon();
                    }

                    @Override
                    public void onError(Throwable e) {
                        String tip; // different tips, according to the action(refresh or load more)
                        if (isLoadMore) {
                            tip = "No more data or connectivity error...";
                        } else {
                            tip = "Request error...";
                            showEmptyList();
                        }
                        Snackbar snackbar = Snackbar.make(gankRecyclerView, tip, Snackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                        hideRefreshIcon();
                    }

                    @Override
                    public void onNext(List<Gank> ganks) {

                        // if load more, add items to the list
                        // else refresh all items
                        if (isLoadMore) {
                            gankAdapter.addItems(ganks);
                            Toast.makeText(FirstActivity.this, "More ganks got!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (ganks.size() > 0) {
                                showNormalList();
                                gankAdapter.refreshItems(ganks);
                                Toast.makeText(FirstActivity.this, "New ganks got!", Toast.LENGTH_SHORT).show();
                            } else {
                                showEmptyList();
                                Toast.makeText(FirstActivity.this, "No ganks on server(T_T", Toast.LENGTH_SHORT).show();
                            }

                        }

                        hideRefreshIcon();
                    }
                });
    }

    private void showNormalList() {
        emptyLayout.setVisibility(View.GONE);
        gankRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showEmptyList() {
        emptyLayout.setVisibility(View.VISIBLE);
        gankRecyclerView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (gankListSubscription != null && !gankListSubscription.isUnsubscribed()) {
            gankListSubscription.unsubscribe();
        }

        new SetHasReadTask(this)
                .executeOnExecutor(ThreadUtils.getWorkerThreads(), gankAdapter.getHasReadSet()); // consider to put this in 'onPause'

        super.onDestroy();
    }

    private long last = 0;
    private final int EXIT_INTERVAL = 700;
    @Override
    public void onBackPressed() {
        long cur = System.currentTimeMillis();
        if (cur - last < EXIT_INTERVAL) {
            finish();
        } else {
            last = cur;
            Toast.makeText(FirstActivity.this, "One more press to quit.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(int position) {
        Logger.log("go to: " + (position+1));

        /*Intent intent = new Intent(FirstActivity.this, GankDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(GankDetailActivity.URL_KEY, gankList.get(position).getUrl());
        bundle.putString(GankDetailActivity.TITLE_KEY, gankList.get(position).getDesc());
        intent.putExtras(bundle);
        startActivity(intent);*/

        Intent intent = new Intent(FirstActivity.this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DetailActivity.URL_KEY, gankList.get(position).getUrl());
        bundle.putString(DetailActivity.TITLE_KEY, gankList.get(position).getDesc());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // hide the icon which appears in the process of refreshing
    private void hideRefreshIcon() {
        if (gankSwipeRefreshLayout.isRefreshing()) {
            gankSwipeRefreshLayout.setRefreshing(false);
        }
    }

    // a way to solve the 'bug' of refreshing of swipe refresh layout
    private void setSwipeLoading(final boolean loading) {
        gankSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                gankSwipeRefreshLayout.setRefreshing(loading);
            }
        });
    }

    // (Automatically)load more, when scrolling to the bottom of the list.
    private void loadMore() {
        isLoadMore = true;
        ++pageIndex;
        gankSwipeRefreshLayout.setRefreshing(true);
        requestData();
    }

    // scroll to the top of the list
    private void goBackTopOfList() {
        gankRecyclerView.smoothScrollToPosition(0);
    }

    /* Load the set that keeps the ids of ganks that has been read. */
    public static class GetHasReadTask extends AsyncTask<Void, Void, Set<String>> {

        SoftReference<BaseActivity> activitySoftReference = null;

        public GetHasReadTask(BaseActivity activity) {
            activitySoftReference = new SoftReference<>(activity);
        }

        @Override
        protected Set<String> doInBackground(Void... voids) {
            return (Set<String>) FileUtils.retrieveObject(Constants.FILE_GANKS_HAS_READ);
        }

        @Override
        protected void onPostExecute(Set<String> strings) {
            if (activitySoftReference != null) {
                ((FirstActivity)activitySoftReference.get()).gankAdapter.setHasReadSet(strings);
            }
            activitySoftReference = null;
        }
    }

    /* Set the has-read set. */
    public static class SetHasReadTask extends AsyncTask<Set<String>, Void, Void> {

        SoftReference<BaseActivity> activitySoftReference = null;

        public SetHasReadTask(BaseActivity activity) {
            activitySoftReference = new SoftReference<>(activity);
        }

        @Override
        protected Void doInBackground(Set<String>... sets) {
            Set<String> set = sets[0];
            FileUtils.saveObject(Constants.FILE_GANKS_HAS_READ, set);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            activitySoftReference = null;
        }
    }
}
