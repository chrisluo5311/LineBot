package com.infotran.springboot.util;

import okhttp3.OkHttpClient;
import javax.net.ssl.X509TrustManager;

public interface ClientUtil {


    X509TrustManager manager = SSLSocketClientUtil.getX509TrustManager();

    OkHttpClient client = new OkHttpClient.Builder()
            .sslSocketFactory(SSLSocketClientUtil.getSocketFactory(manager), manager)// 忽略校验
            .hostnameVerifier(SSLSocketClientUtil.getHostnameVerifier())//忽略校验
            .build();
}
