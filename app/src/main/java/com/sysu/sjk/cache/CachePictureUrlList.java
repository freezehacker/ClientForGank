package com.sysu.sjk.cache;

import com.sysu.sjk.utils.FileUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by sjk on 16-11-3.
 */
public class CachePictureUrlList {

    public static final String FILE_NAME = "picture_url_list";

    public static void save(List<String> pictureUrlList) {
        FileUtils.saveObject(FILE_NAME, pictureUrlList);
    }

    public static List<String> retrieve() {
        return (List<String>)FileUtils.retrieveObject(FILE_NAME);
    }
}
