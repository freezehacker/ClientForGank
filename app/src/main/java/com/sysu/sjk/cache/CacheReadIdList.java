package com.sysu.sjk.cache;

import com.sysu.sjk.bean.Gank;
import com.sysu.sjk.utils.FileUtils;

import java.util.Set;

/**
 * Created by sjk on 16-10-29.
 */
public class CacheReadIdList {

    public static final String FILE_NAME = "read_id_list";

    public static void save(Set<String> allGankList) {
        FileUtils.saveObject(FILE_NAME, allGankList);
    }

    public static Set<String> retrieve() {
        return (Set<String>)FileUtils.retrieveObject(FILE_NAME);
    }
}
