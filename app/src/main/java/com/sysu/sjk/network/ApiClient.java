package com.sysu.sjk.network;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sjk on 16-10-21.
 */
public class ApiClient {

    public static ApiService apiService = null;

    public static ApiService getService() {
        if (apiService == null) {
            // can set some params, such as timeout
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(NetworkConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(NetworkConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(NetworkConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(NetworkConfig.GANK_BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            apiService = retrofit.create(ApiService.class);
        }

        return apiService;
    }
}
