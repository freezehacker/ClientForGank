package com.sysu.sjk.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sjk on 16-10-23.
 */
public class ThreadUtils {

    private static ExecutorService executorService = null;

    public static ExecutorService getWorkerThreads() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(3);
        }

        return executorService;
    }
}
