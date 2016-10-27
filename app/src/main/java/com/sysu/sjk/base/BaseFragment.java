package com.sysu.sjk.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sjk on 16-10-21.
 */
public abstract class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(getLayoutId(), container, false);
            initView();
            initData();
            initListener();
        } else {
            // view has existed, just restore it, instead of re-inflating it
            onRestoreView();
        }

        return mView;
    }

    protected View mView;

    /**
     * custom callback of fragment, to restore the view(mView) that hasn't been recycled
     * user should override this callback, to realize his operations
     */
    public abstract void onRestoreView();

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initData();

    public abstract void initListener();
}
