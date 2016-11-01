package com.sysu.sjk.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.sysu.sjk.bean.Gank;
import com.sysu.sjk.cache.CacheAllGankList;
import com.sysu.sjk.utils.Logger;
import com.sysu.sjk.view.FirstActivity;

import java.util.List;

/**
 * Created by sjk on 16-10-29.
 */
public class CacheAllGankListService extends IntentService {

    public static final String ACTION_CACHE_ALL_GANK_LIST = "com.sysu.sjk.cache_all_gank_list";
    public static final String INTENT_KEY = "parcelable_list_key";

    public CacheAllGankListService() {
        super("CacheAllGankListService");
    }

    public CacheAllGankListService(String name) {
        super(name);
    }

    // 1.automatically execute in a new thread
    // 2.automatically finish itself in the end
    @Override
    protected void onHandleIntent(Intent intent) {
        //Logger.log("call on CacheAllGankListService: onHandleIntent()");

        List<Gank> gankList = intent.getParcelableArrayListExtra(INTENT_KEY);
        // firstly decide whether there's a need to save
        List<Gank> oldGankList = CacheAllGankList.retrieve();

        if (oldGankList != null && oldGankList.equals(gankList)) {
            sendBroadcast(new Intent(FirstActivity.CacheAllGankListReceiver.NO_NEED_TO_CACHE_GANK_LIST));
        } else {
            CacheAllGankList.save(gankList);
            sendBroadcast(new Intent(FirstActivity.CacheAllGankListReceiver.CACHE_GANK_LIST_FINISHED));
        }
    }
}
