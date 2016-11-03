package com.sysu.sjk.network;

import com.sysu.sjk.bean.ApiResponse;
import com.sysu.sjk.bean.Gank;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by sjk on 16-10-21.
 */
public interface ApiService {

    @GET("Android/{pageSize}/{pageIndex}")
    Observable<ApiResponse<Gank>> getAndroidGanksByPage(@Path("pageSize") int pageSize, @Path("pageIndex") int pageIndex);

    @GET("%E7%A6%8F%E5%88%A9/{pageSize}/{pageIndex}")
    Observable<ApiResponse<Gank>> getPicturesByPage(@Path("pageSize") int pageSize, @Path("pageIndex") int pageIndex);
}
