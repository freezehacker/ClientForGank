package com.sysu.sjk.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.sjk.bean.Gank;
import com.sysu.sjk.utils.Logger;
import com.sysu.sjk.view.R;
import com.sysu.sjk.view.tmp_interface.MyClickListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sjk on 16-10-22.
 */
public class GankListAdapter extends RecyclerView.Adapter<GankListAdapter.GankListViewHolder> {

    private Context mContext;
    private List<Gank> mGankList;
    private MyClickListener mMyClickListener;

    private Set<String> hasReadSet; // keep the ids that has been read

    public GankListAdapter(Context context, List<Gank> gankList) {
        mContext = context;
        mGankList = gankList;
        //hasReadSet = new HashSet<>();
    }

    // exposed to the activity, after being read from file in a new thread
    public void setHasReadSet(Set<String> hasReadSet) {
        this.hasReadSet = hasReadSet;
        if (hasReadSet == null) {
            this.hasReadSet = new HashSet<>();  // to avoid NPE in the following
        }
    }

    // exposed to the activity, before being written to file in a new thread
    public Set<String> getHasReadSet() {
        return this.hasReadSet;
    }

    // a public interface for user to set a listener
    public void setMyClickListener(MyClickListener myClickListener) {
        mMyClickListener = myClickListener;
    }

    // add more items
    public void addItems(List<Gank> ganks) {
        int start, count;
        start = mGankList.size();
        count = ganks.size();
        mGankList.addAll(ganks);
        notifyItemRangeInserted(start, count);
    }

    // add new items
    public void refreshItems(List<Gank> ganks) {
        mGankList.clear();
        mGankList.addAll(ganks);
        notifyDataSetChanged();
    }

    @Override
    public GankListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View baseView = LayoutInflater.from(mContext).inflate(R.layout.item_gank, parent, false);
        GankListViewHolder ret = new GankListViewHolder(baseView);
        return ret;
    }

    @Override
    public void onBindViewHolder(final GankListViewHolder holder, final int position) {
        final Gank gank = mGankList.get(position);

        holder.title.setText(filterTitle(gank.getDesc(), position));
        if (hasReadSet.contains(gank.get_id())) {
            Logger.log((position + 1) + " has read");
            holder.title.setTextColor(mContext.getResources().getColor(R.color.secondText));
        } else {
            Logger.log((position + 1) + " NOT read");
            holder.title.setTextColor(mContext.getResources().getColor(R.color.primaryText));
        }

        holder.date.setText(filterDate(gank.getPublishedAt()));
        holder.who.setText(filterPublisher(gank.getWho()));

        /*((View) holder.title.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "hello: " + position, Toast.LENGTH_SHORT).show();
            }
        });*/

        if (mMyClickListener != null) {
            ((View) holder.title.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hasReadSet.add(gank.get_id());  // Mark that this has been read.
                    notifyItemChanged(position);    // To change its color.
                    mMyClickListener.onClick(position);
                }
            });
        }
    }

    private String filterTitle(String title, int position) {
        return (position + 1) + ". " + title;
    }

    private String filterDate(String date) {
        int delimIndex = date.indexOf('T');
        if (delimIndex == -1) {
            return date;
        } else {
            return date.substring(0, delimIndex);
        }
    }

    private String filterPublisher(String publisher) {
        return "by " + publisher;
    }

    @Override
    public int getItemCount() {
        return mGankList.size();
    }


    // ViewHolder class
    public static class GankListViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView who;
        private TextView date;

        public GankListViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.gank_list_item_title);
            who = (TextView) itemView.findViewById(R.id.gank_list_item_who);
            date = (TextView) itemView.findViewById(R.id.gank_list_item_date);
        }
    }
}
