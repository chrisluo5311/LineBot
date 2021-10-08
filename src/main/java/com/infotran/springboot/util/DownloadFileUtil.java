package com.infotran.springboot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author chris
 * */
@Component
@Slf4j
public class DownloadFileUtil {

    //檔案路徑
    public static final String filePath = "/static/";

    private static final String CHARSET = "UTF-8";

    /**
     * 使用URL連結並用Java IO 和 Java NET寫出檔案後回傳檔案字串
     *
     * @param govURL 政府公開csv檔URL
     * @param fileName 檔案名稱
     * @return String file的內容文字
     * */
    public static String downloadByUrl(String govURL,String fileName) throws IOException {
        URL url = new URL(govURL);
        String strFileContents = null;
        try (
            InputStream inputStream = url.openStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            FileOutputStream fileOutputStream = new FileOutputStream(filePath+fileName,false);
        ) {
            byte[] bucket = new byte[2048];
            int numBytesRead = 0;
            while ((numBytesRead = bufferedInputStream.read(bucket, 0, bucket.length)) != -1) {
                fileOutputStream.write(bucket, 0, numBytesRead);
                strFileContents += new String(bucket, 0, numBytesRead);
            }
        }
        return strFileContents;
    }

    /**
     * 使用字串並用Java IO 和 Java NET寫出檔案後回傳檔案字串
     *
     * @param str 例: svg標籤
     * @param fileName 檔案名稱
     * @return String filePath檔案路徑
     * */
    public static String downloadByString(String str,String fileName) throws IOException {
        String completeFilePath = filePath+fileName;
//        String strFileContents = null;
        try (
                InputStream inputStream = convertStr2InputStream(str);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(completeFilePath,false);
        ) {
            byte[] bucket = new byte[2048];
            int numBytesRead = 0;
            while ((numBytesRead = bufferedInputStream.read(bucket, 0, bucket.length)) != -1) {
                fileOutputStream.write(bucket, 0, numBytesRead);
//                strFileContents += new String(bucket, 0, numBytesRead);
            }
        }
        return completeFilePath;
    }


    /**
     * 使用File類寫出檔案並回傳字串集合
     *
     * @param govURL 政府公開csv檔URL
     * @return List
     * */
    public static List<String> downloadWithFilesCopy(String govURL,String fileName) throws IOException {
        URL url = new URL(govURL);
        List<String> strFileContents = new ArrayList<>();
        try(InputStream inputStream = url.openStream()){
            Files.copy(inputStream, Paths.get(filePath+fileName));
            Files.readAllLines(Paths.get(url.toURI())).stream().map(String::trim).forEach(strFileContents::add);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return strFileContents;
    }

    /**
     *  使用HttpClient解析檔案回傳字串
     *
     *  @param govURL 政府公開csv檔URL
     *  @return String
     * */
    public static String downlaodWithHttpClient(String govURL) throws Exception {
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(new URI(govURL))
                .GET()
                .build();
          //寫出檔案的寫法
//        HttpResponse<InputStream> response = httpClient
//                .send(httpRequest, responseInfo ->
//                        HttpResponse.BodySubscribers.ofInputStream());
        HttpResponse<String> response = httpClient
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     *  使用HttpClient異部請求解析檔案回傳字串
     *
     *  @param govURL 政府公開csv檔URL
     *  @return String
     * */
    public static String downlaodWithHttpClientAsync(String govURL) throws Exception {
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(new URI(govURL))
                .GET()
                .build();

        Future<String> futureString =
                httpClient
                        .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body);
        return futureString.get();
    }

    /**
     *  使用Apache Common IO寫出字串
     *
     *  @param govURL 政府公開csv檔URL
     *  @return String
     * */
    public static String downloadWithApacheCommonIO(String govURL) throws IOException {
        File file = new File(URI.create(govURL));
        return FileUtils.readFileToString(file, "UTF-8");
    }


    /**
     *  本地檔案的路徑轉URI，使用File
     *
     *  @param path 本地檔案路徑
     *  @return URI
     * */
    public static URI file2URI(String path) {
        return (path==null)?null:new File(path).toURI();
    }

    /**
     *  本地檔案的路徑轉URI，使用Paths
     *
     *  @param path 本地檔案路徑
     *  @return URI
     * */
    public static URI path2URI(String path){
        return (path==null)?null:Paths.get(path).toUri();
    }


    private static InputStream convertStr2InputStream(String str) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(str.getBytes(CHARSET));
    }

    private static InputStream convertStr2InputStreamWithIO(String str) throws IOException {
        return IOUtils.toInputStream(str,CHARSET);
    }

}
