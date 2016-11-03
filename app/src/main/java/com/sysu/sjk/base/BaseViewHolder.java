package com.sysu.sjk.base;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sjk on 16-11-2.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    Context mContext;
    View mConvertView;          // assigned, got from callback function 'getView()'
    SparseArray<View> mViews;   // to cache the child views in the view holder

    /* Factory */
    public static BaseViewHolder create(Context context, ViewGroup viewGroup, int layoutId) {
        View view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);
        BaseViewHolder ret = new BaseViewHolder(context, view, viewGroup);
        return ret;
    }

    public BaseViewHolder(Context context, View itemView, View parentView) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    /*
    *   Find view by the view id(R.id.XXX).
    *   This method is a replacement of [findViewById(int)].
    */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T)view;
    }
}
