package com.leaps.scaffold.net;

import okhttp3.OkHttpClient;

public interface OkHttpFactory {
    OkHttpClient getClient();
}
