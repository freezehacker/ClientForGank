package com.sysu.sjk.custom_view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by sjk on 16-11-2.
 */
public class MyRecyclerView extends RecyclerView {



    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {

    }

    public interface LoadMoreListener {
        void loadMore();
    }

    public interface RefreshListener {
        void refresh();
    }
}
