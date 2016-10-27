package com.sysu.sjk.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sjk on 16-10-21.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData();
        initListener();
    }

    // set layout for this activity
    public abstract int getLayoutId();

    // findViewById()
    public abstract void initView();

    // initialize the data when firstly enter
    public abstract void initData();

    // add necessary listeners for the activity
    public abstract void initListener();
}
