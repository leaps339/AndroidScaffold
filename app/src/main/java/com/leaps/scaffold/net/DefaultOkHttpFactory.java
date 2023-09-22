package com.leaps.scaffold.net;

import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

/**
 * 默认OkHttpClient构造工厂
 *
 */
public class DefaultOkHttpFactory implements OkHttpFactory {

    @Override
    public OkHttpClient getClient() {

        return new OkHttpClient.Builder()
                .sslSocketFactory(SSLFactory.sslContext.getSocketFactory(), SSLFactory.xtm)
                .hostnameVerifier(SSLFactory.DO_NOT_VERIFY)
//                .addInterceptor(OkHttpQualityInterceptorManager.getOkHttpErrorInterceptor())
                .callTimeout(10_000, TimeUnit.MILLISECONDS)
                .readTimeout(20_000, TimeUnit.MILLISECONDS)
                .writeTimeout(20_000, TimeUnit.MILLISECONDS)
                .build();

    }
}
