package com.sysu.sjk.view.tmp_interface;

import android.view.View;

/**
 * Created by sjk on 16-10-22.
 *
 * Custom clicking callback.
 * Move the control from recycler view's adapter to the activity(or fragment) that uses it.
 */
public interface MyClickListener {

    void onClick(int position);
}
