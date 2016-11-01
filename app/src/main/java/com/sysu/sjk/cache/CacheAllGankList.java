package com.sysu.sjk.cache;

import com.sysu.sjk.bean.Gank;
import com.sysu.sjk.utils.FileUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by sjk on 16-10-29.
 */
public class CacheAllGankList {

    public static final String FILE_NAME = "all_gank_list";

    public static void save(List<Gank> allGankList) {
        FileUtils.saveObject(FILE_NAME, allGankList);
    }

    public static List<Gank> retrieve() {
        return (List<Gank>)FileUtils.retrieveObject(FILE_NAME);
    }
}
