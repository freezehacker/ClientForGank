package com.sysu.sjk.view;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.sjk.base.BaseActivity;
import com.sysu.sjk.bean.Gank;
import com.sysu.sjk.dao.GankDao;
import com.sysu.sjk.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjk on 16-10-25.
 *
 * Provide a visualized UI to test the DAO of class Gank.
 */
public class TestDaoActivity extends BaseActivity {

    GankDao gankDao;
    TextInputLayout idWrapper, descWrapper, whoWrapper;
    FloatingActionButton addButton;
    ListView listView;
    VisualDaoAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_test_dao;
    }

    @Override
    public void initView() {
        listView = (ListView) findViewById(R.id.test_dao_list_view);

        addButton = (FloatingActionButton) findViewById(R.id.gank_dao_add_gank);

        idWrapper = (TextInputLayout) findViewById(R.id.gank_dao_input_wrapper_id);
        idWrapper.setHintEnabled(true);
        idWrapper.setHint("Id");

        descWrapper = (TextInputLayout) findViewById(R.id.gank_dao_input_wrapper_desc);
        descWrapper.setHintEnabled(true);
        descWrapper.setHint("Title");

        whoWrapper = (TextInputLayout) findViewById(R.id.gank_dao_input_wrapper_who);
        whoWrapper.setHintEnabled(true);
        whoWrapper.setHint("Author");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void initData() {
        gankDao = new GankDao(TestDaoActivity.this);
        //gankDao.deleteAllGanks();

        adapter = new VisualDaoAdapter(TestDaoActivity.this);
        listView.setAdapter(adapter);

        updateListInDB();
    }

    @Override
    public void initListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = idWrapper.getEditText().getText().toString();
                String desc = descWrapper.getEditText().getText().toString();
                String who = whoWrapper.getEditText().getText().toString();

                if (TextUtils.isEmpty(id)) {
                    idWrapper.setError("Id can't be empty!");
                    return;
                }
                
                Gank gank = new Gank(id);
                gank.setDesc(desc);
                gank.setWho(who);

                gankDao.saveGank(gank);

                updateListInDB();
            }
        });
    }

    // refresh the list, according to the data in DB.
    private void updateListInDB() {
        List<Gank> queriedList = gankDao.getAllGank();
        adapter.refreshItems(queriedList);


        Logger.log("List updated: " + queriedList);
        Toast.makeText(TestDaoActivity.this, "refresh", Toast.LENGTH_SHORT).show();
    }

    /**
     * Adapter of gank list.
     */
    public class VisualDaoAdapter extends BaseAdapter {

        Context context;
        List<Gank> gankList;

        public VisualDaoAdapter(Context context) {
            this.context = context;
            gankList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return gankList.size();
        }

        @Override
        public Object getItem(int i) {
            return gankList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            Gank gank = gankList.get(i);
            VisualDaoViewHolder viewHolder = null;
            if (convertView != null) {
                viewHolder = (VisualDaoViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_gank_test_dao, viewGroup, false);
                viewHolder = new VisualDaoViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            viewHolder.itemId.setText(gank.get_id());

            //...

            return convertView;
        }

        /*// add ganks
        public void addItems(List<Gank> extraGanks) {
            gankList.addAll(extraGanks);
            notifyDataSetChanged();
        }*/

        // refresh ganks
        public void refreshItems(List<Gank> newGanks) {
            gankList.clear();
            gankList.addAll(newGanks);
            notifyDataSetChanged();
        }


        /**
         * ViewHolder class for gank list's adapter.
         */
        public class VisualDaoViewHolder {
            TextView itemId;

            public VisualDaoViewHolder(View itemView) {
                itemId = (TextView) itemView.findViewById(R.id.test_dao_item_id);
            }
        }
    }
}
