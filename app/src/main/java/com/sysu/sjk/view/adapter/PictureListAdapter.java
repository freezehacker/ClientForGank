package com.sysu.sjk.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.sjk.base.BaseAdapter;
import com.sysu.sjk.base.BaseViewHolder;
import com.sysu.sjk.cache.ImageCache;
import com.sysu.sjk.utils.Logger;
import com.sysu.sjk.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjk on 16-11-1.
 */
public class PictureListAdapter extends BaseAdapter<String> {

    ImageCache cache;

    public PictureListAdapter(Context context) {
        super(context, R.layout.item_picture_list);
        cache = ImageCache.getInstance(context);
    }

    @Override
    public void convert(BaseViewHolder holder, String url) {
        // firstly set an empty picture
        ImageView iv = holder.getView(R.id.picture_item_pic);
        iv.setImageResource(R.drawable.heart);

        // then set the right picture
        //Logger.log("convert: " + url);
        cache.setImage(iv, url);
    }
}
