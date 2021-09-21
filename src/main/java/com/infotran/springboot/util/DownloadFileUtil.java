package com.infotran.springboot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
    private static String filePath = "D:\\IdeaProject\\LineBot\\src\\main\\resources";

    //檔案名稱
    private static String fileName = "Mask_CSV.csv";

    /**
     * 使用Java IO 和 Java NET寫出檔案並回傳字串
     * @param govURL 政府公開csv檔URL
     * @return String
     * */
    public static String downloadWithIO(String govURL) throws IOException {
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
     * 使用File類寫出檔案並回傳字串集合
     * @param govURL 政府公開csv檔URL
     * @return List
     * */
    public static List<String> downloadWithFilesCopy(String govURL) throws IOException {
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
     *  @param govURL 政府公開csv檔URL
     *  @return String
     * */
    public static String downloadWithApacheCommonIO(String govURL) throws IOException {
        File file = new File(URI.create(govURL));
        return FileUtils.readFileToString(file, "UTF-8");
    }


    /**
     *  本地檔案的路徑轉URI，使用File
     *  @param path 本地檔案路徑
     *  @return URI
     * */
    public static URI file2URI(String path) {
        if (path==null) return null;
        return new File(path).toURI();
    }

    /**
     *  本地檔案的路徑轉URI，使用Paths
     *  @param path 本地檔案路徑
     *  @return URI
     * */
    public static URI path2URI(String path){
        if (path==null) return null;
        return Paths.get(path).toUri();
    }


}
