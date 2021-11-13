package com.infotran.springboot.util;

import okhttp3.OkHttpClient;
import javax.net.ssl.X509TrustManager;

/**
 * OkHttpClient
 * 忽略ssl校驗
 * @author chris
 */
public interface ClientUtil {


    X509TrustManager MANAGER = SSLSocketClientUtil.getX509TrustManager();

    OkHttpClient CLIENT = new OkHttpClient.Builder()
                                          .sslSocketFactory(SSLSocketClientUtil.getSocketFactory(MANAGER), MANAGER)
                                          .hostnameVerifier(SSLSocketClientUtil.getHostnameVerifier())
                                          .build();
}
