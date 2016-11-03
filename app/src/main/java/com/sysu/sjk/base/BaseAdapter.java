package com.sysu.sjk.base;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjk on 16-11-2.
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    Context mContext;
    LayoutInflater mLayoutInflater;
    int mResId;
    List<T> mDatas;

    RecyclerView mRecyclerView;

    public BaseAdapter(Context context, int resId) {
        mContext = context;
        mResId = resId;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDatas = new ArrayList<>();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // get the self recycler view
        mRecyclerView = recyclerView;
    }

    public void refreshItems(List<T> data) {
        mDatas.clear();
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    public void addItems(List<T> data) {
        int start, count;
        start = mDatas.size();
        count = data.size();
        mDatas.addAll(data);
        notifyItemRangeInserted(start, count);
    }

    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(mResId, parent, false);
        BaseViewHolder ret = new BaseViewHolder(mContext, itemView, parent);

        return ret;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        convert(holder, mDatas.get(position));

        if (mOnItemClickListener != null) {
            holder.mConvertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(view, position);
                }
            });
        }
    }

    /* Descendant must extends this method, to bind data in a view holder. */
    public abstract void convert(BaseViewHolder holder, T item);


    private OnItemClickListener mOnItemClickListener = null;
    private OnLoadMoreListener mOnLoadMoreListener = null;
    private OnRefreshListener mOnRefreshListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;

        if (mOnLoadMoreListener != null) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                private int lastItemPos = 0;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    // load more when it's in idle state
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (lastItemPos + 1 == getItemCount()) {    // can be modified here
                            mOnLoadMoreListener.onLoadMore();
                        }
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    // update the last position
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        lastItemPos = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        lastItemPos = ((StaggeredGridLayoutManager)layoutManager).findLastVisibleItemPositions(null)[0];
                    }
                }
            });
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}
