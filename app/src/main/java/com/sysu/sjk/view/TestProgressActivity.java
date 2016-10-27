package com.sysu.sjk.view;

import android.view.View;

import com.sysu.sjk.base.BaseActivity;
import com.sysu.sjk.custom_view.RoundProgressView;
import com.sysu.sjk.utils.Logger;

/**
 * Created by sjk on 16-10-23.
 */
public class TestProgressActivity extends BaseActivity {

    RoundProgressView rpv;

    @Override
    public int getLayoutId() {
        return R.layout.activity_test_progress;
    }

    @Override
    public void initView() {
        rpv=(RoundProgressView)findViewById(R.id.rpv_test);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        rpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Logger.log("click");

                rpv.setProgress(rpv.getProgress() + 4);
            }
        });
    }
}
