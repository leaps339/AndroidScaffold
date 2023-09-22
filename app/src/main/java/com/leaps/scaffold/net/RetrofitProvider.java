package com.leaps.scaffold.net;

import android.text.TextUtils;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitProvider {
    private static final String TAG = "RetrofitProvider";

    private RetrofitProvider() {
        okHttpFactory = new DefaultOkHttpFactory();
    }

    private static class SingletonHandler {
        private static RetrofitProvider instance = new RetrofitProvider();
    }

    public static RetrofitProvider getInstance() {
        return SingletonHandler.instance;
    }

    private HashMap<String, Retrofit> retrofitCache = new HashMap<>();
    private Retrofit.Builder mBuilder;

    private OkHttpFactory okHttpFactory;

    private boolean loggable = false;

    /**
     * 创建根据url创建接口服务对象
     *
     * @param url   请求的baseUrl，需按照retrofit构造标准以'/'结尾
     * @param clazz service类型
     * @return service对应的实例
     */
    public <T> T createService(final String url, Class<T> clazz) {
        String baseUrl = url;
        final ApiUrl apiUrl = clazz.getAnnotation(ApiUrl.class);
        if (apiUrl != null) {
            baseUrl = getBaseUrl(apiUrl.value());
        }

        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("url and service ApiUrl annotation both empty!!");
        }

        return getRetrofit(baseUrl).create(clazz);
    }

    public <T> T createService(Class<T> clazz) {
        return createService(null, clazz);
    }

    /**
     * 设置OkHttpClient
     */
    public void setOkHttpFactory(OkHttpFactory factory) {
        okHttpFactory = factory;
    }

    private Retrofit getRetrofit(String url) {
        Retrofit retrofit = null;
        if (retrofitCache.containsKey(url)) {
            retrofit = retrofitCache.get(url);
        }

        if (retrofit == null) {
            retrofit = createRetrofit(url);

            retrofitCache.put(url, retrofit);
        }

        return retrofit;
    }

    private Retrofit createRetrofit(String url) {
        if (mBuilder == null) {
            mBuilder = createBuilder();
        }

        return mBuilder.baseUrl(url).build();
    }

    private Retrofit.Builder createBuilder() {
        OkHttpClient client = okHttpFactory.getClient();

        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client);
    }

    // 根据key获取host域名
    private String getBaseUrl(String hostKey) {
        return "";
    }
}