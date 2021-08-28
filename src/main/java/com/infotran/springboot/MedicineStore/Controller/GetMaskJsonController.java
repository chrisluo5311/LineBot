package com.infotran.springboot.MedicineStore.Controller;

import com.infotran.springboot.ConfirmCase.model.ConfirmCase;
import com.infotran.springboot.MedicineStore.Model.MedicineStore;
import com.infotran.springboot.MedicineStore.Service.Impl.MedicinetoreServiceImpl;
import com.infotran.springboot.MedicineStore.Service.MedicineStoreService;
import com.infotran.springboot.config.SSLSocketClientUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;

import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@Slf4j
public class GetMaskJsonController {

    private String mask_URL = "https://raw.githubusercontent.com/kiang/pharmacies/master/json/points.json";

    private OkHttpClient okHttpClient;

    private static OkHttpClient client ;

    private MedicineStore medicineStore;

    @Autowired
    private MedicineStoreService medicinetoreService;

    static {
        X509TrustManager manager = SSLSocketClientUtil.getX509TrustManager();
        client = new OkHttpClient.Builder()
                .sslSocketFactory(SSLSocketClientUtil.getSocketFactory(manager), manager)// 忽略校验
                .hostnameVerifier(SSLSocketClientUtil.getHostnameVerifier())//忽略校验
                .build();
    }

    public void run() throws IOException {
        Request request = new Request.Builder().url(mask_URL).get().build(); // get post put 等
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @SneakyThrows
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonBody = response.body().string();
//              log.info(jsonBody);
                parseMaskInfo(jsonBody);
            }
        });
    }


    public void parseMaskInfo (String jsonBody) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonBody);
        JSONArray jsonArray = jsonObject.getJSONArray("features");
        for (int i = 0 ; i < 1 ; i++){
            medicineStore = new MedicineStore();
            JSONObject Feature = jsonArray.getJSONObject(i);
            JSONObject property = Feature.getJSONObject("properties");
            JSONObject geometry = Feature.getJSONObject("geometry");
            if (property.has("id")) medicineStore.setId(Long.parseLong(property.getString("id")));
            if (property.has("name")) medicineStore.setName(property.getString("name"));
            if (property.has("phone")) medicineStore.setPhoneNumber(property.getString("phone"));
            if (property.has("address")) medicineStore.setAddress(property.getString("address"));
            if (property.has("mask_adult")) medicineStore.setMaskAdult(Integer.parseInt(property.getString("mask_adult")));
            if (property.has("mask_child")) medicineStore.setMaskChild(Integer.parseInt(property.getString("mask_child")));
            if (property.has("updated")) {
                StringBuilder sb = new StringBuilder();
                sb.append(property.getString("updated").substring(0,4));
                sb.append(property.getString("updated").substring(5,8));
                sb.append(property.getString("updated").substring(9));
                medicineStore.setUpdateTime(sb.toString());
            }
            if (geometry.has("coordinates")) {
                JSONArray coordinates= geometry.getJSONArray("coordinates");
                medicineStore.setLongitude((Double) coordinates.get(0));
                medicineStore.setLatitude((Double)coordinates.get(1));
            }
            medicinetoreService.save(medicineStore);
        }
    }




}
