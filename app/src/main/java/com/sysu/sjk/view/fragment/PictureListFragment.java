package com.sysu.sjk.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.sysu.sjk.base.BaseAdapter;
import com.sysu.sjk.base.BaseFragment;
import com.sysu.sjk.bean.ApiResponse;
import com.sysu.sjk.bean.Gank;
import com.sysu.sjk.cache.CachePictureUrlList;
import com.sysu.sjk.network.ApiClient;
import com.sysu.sjk.network.MockServer;
import com.sysu.sjk.utils.Logger;
import com.sysu.sjk.utils.ThreadUtils;
import com.sysu.sjk.view.PictureDetailActivity;
import com.sysu.sjk.view.R;
import com.sysu.sjk.view.adapter.PictureListAdapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by sjk on 16-11-1.
 */
public class PictureListFragment extends BaseFragment {

    RecyclerView pictureListRecyclerView;
    SwipeRefreshLayout pictureListSwipe;
    PictureListAdapter pictureListAdapter;
    Subscription pictureListSubscription;
    Toolbar pictureListToolbar;

    private static final int PAGE_SIZE = 10;
    private boolean isFirstLoad = true;
    private boolean isLoadMore = false;         // a sign to mark the request type
    private boolean isLoadingMore = false;      // whether it's loading more
    private boolean isRefreshing = false;       // whether it's refreshing
    private int pageIndex = 1;  // Note:first page is 1, not 0.
    StaggeredGridLayoutManager layoutManager;
    private int[] lastPositions = new int[2];


    @Override
    public void onRestoreView(Bundle savedInstanceState) {
        Logger.log("onRestoreView(Bundle)");
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_picture_list;
    }

    @Override
    public void initView() {
        pictureListRecyclerView = (RecyclerView) mView.findViewById(R.id.picture_recycler_view);
        pictureListSwipe = (SwipeRefreshLayout) mView.findViewById(R.id.picture_list_swipe);
        pictureListToolbar = (Toolbar) mView.findViewById(R.id.picture_list_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(pictureListToolbar); // ...
        configPictureRecyclerView();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        ////mockData();

        // retrieve old data
        new GetPictureUrlListTask(this)
                .executeOnExecutor(ThreadUtils.getWorkerThreads());

        // request new data
        request();
        pictureListSwipe.post(new Runnable() {
            @Override
            public void run() {
                pictureListSwipe.setRefreshing(true);
            }
        });
    }

    /* Create some fake data, to make a small test. */
    private void mockData() {
        pictureListAdapter.refreshItems(Arrays.asList(MockServer.SMALL_IMAGE_URLS));
    }

    @Override
    public void onDestroyView() {
        if (pictureListSubscription != null) {
            pictureListSubscription.unsubscribe();
        }
        super.onDestroyView();
    }

    @Override
    public void initListener() {
        pictureListSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                beforeRefresh();
                request();
            }
        });

        pictureListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // because span count is 2, so a 2-dimension array is needed
                // to denote positions of the last 2 element
                int left = lastPositions[0];
                int right = lastPositions[1];

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int count = pictureListAdapter.getItemCount();
                    if (left + 1 == count || right + 1 == count) {
                        if (!isLoadingMore) {   // in case of loading repeatedly
                            Logger.log("准备请求更多...");
                            beforeLoadMore();
                            request();
                        } else {
                            Logger.log("正在加载更多...");
                            Toast.makeText(getActivity(), "正在加载更多...", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastPositions = layoutManager.findLastVisibleItemPositions(lastPositions);
            }
        });
    }

    private void beforeLoadMore() {
        isLoadMore = true;
        isLoadingMore = true;
    }

    private void beforeRefresh() {
        isLoadMore = false;
        isRefreshing = true;
        pageIndex = 1;
    }

    private void finishLoading() {
        if (pictureListSwipe.isRefreshing()) {
            pictureListSwipe.setRefreshing(false);
        }
    }

    private void request() {
        Logger.log("请求数据~~");
        pictureListSubscription = ApiClient.getService()
                .getPicturesByPage(PAGE_SIZE, pageIndex)
                .map(new Func1<ApiResponse<Gank>, List<Gank>>() {
                    @Override
                    public List<Gank> call(ApiResponse<Gank> gankApiResponse) {
                        return gankApiResponse.getResults();
                    }
                })
                .map(new Func1<List<Gank>, List<String>>() {
                    @Override
                    public List<String> call(List<Gank> ganks) {
                        List<String> strings = new ArrayList<String>();
                        for (int i = 0, len = ganks.size(); i < len; ++i) {
                            strings.add(ganks.get(i).getUrl());
                        }
                        return strings;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.log("onError():\n" + e.getMessage());
                        finishLoading();
                        Snackbar.make(pictureListToolbar, "Sorry...服务器错误", Snackbar.LENGTH_SHORT).show();
                        isLoadingMore = false;
                        isRefreshing = false;
                    }

                    @Override
                    public void onNext(List<String> strings) {
                        finishLoading();
                        if (isFirstLoad) {
                            isFirstLoad = false;
                            new SetPictureUrlListTask()
                                    .executeOnExecutor(ThreadUtils.getWorkerThreads(), strings);
                        }
                        if (isLoadMore) {
                            pictureListAdapter.addItems(strings);
                            ++pageIndex;    // add 1 page
                            isLoadingMore = false;
                        } else {
                            pictureListAdapter.refreshItems(strings);
                            isRefreshing = false;
                        }
                        Logger.log("onNext(): " + strings.size());
                        /*for (int i = 0; i < strings.size(); ++i) {
                            Logger.log(strings.get(i));
                        }*/
                    }
                });
    }

    // configure styles of recycler view
    private void configPictureRecyclerView() {
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        pictureListRecyclerView.setLayoutManager(layoutManager);

        pictureListRecyclerView.addItemDecoration(new SpaceItemDeco(6));

        pictureListAdapter = new PictureListAdapter(getActivity());
        pictureListRecyclerView.setAdapter(pictureListAdapter);
        pictureListAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), PictureDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(PictureDetailFragment.PICTURE_KEY, pictureListAdapter.getItem(position));
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });
    }

    /* Space between items. */
    public static class SpaceItemDeco extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDeco(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            /*outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            int pos = parent.getChildAdapterPosition(view);
            if (pos == 0 || pos == 1) {    // if it's on the top
                outRect.top = space;
            }*/

            StaggeredGridLayoutManager.LayoutParams slp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int index = slp.getSpanIndex();
            int position = parent.getChildAdapterPosition(view);
            if (index == 0) {
                outRect.left = space * 2;
                outRect.right = space;
            } else {
                outRect.left = space;
                outRect.right = space * 2;
            }
            outRect.top = (position == 0 || position == 1) ? space * 2 : space;
            outRect.bottom = space;
        }
    }

    public static class GetPictureUrlListTask extends AsyncTask<Void, Void, List<String>> {
        SoftReference<BaseFragment> fragmentSoftReference;

        public GetPictureUrlListTask(BaseFragment fragment) {
            fragmentSoftReference = new SoftReference<>(fragment);
        }

        @Override
        protected List<String> doInBackground(Void... avoid) {
            return CachePictureUrlList.retrieve();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            if (fragmentSoftReference != null) {
                if (strings != null && strings.size() != 0) {
                    ((PictureListFragment)fragmentSoftReference.get()).pictureListAdapter
                            .refreshItems(strings);
                }

                // clear the reference
                fragmentSoftReference = null;
            }
        }
    }

    public static class SetPictureUrlListTask extends AsyncTask<List<String>, Void, Void> {
        @Override
        protected Void doInBackground(List<String>... sets) {
            List<String> urls = sets[0];
            CachePictureUrlList.save(urls);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Logger.log("已缓存url列表。");
        }
    }
}
