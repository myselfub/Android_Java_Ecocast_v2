package com.ecoplay.android.models.network;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRestAPIService {
    private static final String TAG = RetrofitRestAPIService.class.getSimpleName();
    private Map<String, RetrofitRestAPI> retrofitRestAPIMap;
    private OkHttpClient okHttpClient;
    private final String baseURL = "http://10.0.2.2:8000";

    private RetrofitRestAPIService() {
    }

    private static class Singleton {
        private static final RetrofitRestAPIService instance = new RetrofitRestAPIService();
    }

    public static RetrofitRestAPIService getInstance() {
        return Singleton.instance;
    }

    public RetrofitRestAPI getRetrofitRestAPI(String url) {
        if (retrofitRestAPIMap == null) {
            retrofitRestAPIMap = new HashMap<>();
        }

        if (url == null || url.equals("")) {
            url = baseURL;
        }

        if (retrofitRestAPIMap.containsKey(url)) {
            return retrofitRestAPIMap.get(url);
        } else {
            RetrofitRestAPI retrofitRestAPI = createRestClientAPI(url);
            retrofitRestAPIMap.put(url, retrofitRestAPI);

            return retrofitRestAPI;
        }
    }

    private RetrofitRestAPI createRestClientAPI(String url) {
        RetrofitRestAPI retrofitRestAPI = new Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build().create(RetrofitRestAPI.class);

        return retrofitRestAPI;
    }

    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null)
            okHttpClient = createOkHttpClient();
        return okHttpClient;
    }

    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        return builder.build();
    }
}