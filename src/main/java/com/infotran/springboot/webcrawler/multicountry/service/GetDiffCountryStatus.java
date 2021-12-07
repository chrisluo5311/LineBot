package com.infotran.springboot.webcrawler.multicountry.service;

import com.infotran.springboot.util.TimeUtil;
import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;
import com.infotran.springboot.webcrawler.multicountry.service.Impl.DiffCountryServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

/**
 * 取得不同國家的疫情狀態
 *
 * @author chris
 */
@Slf4j
@Service
public class GetDiffCountryStatus {

    private static final String LOG_PREFIX = "GetDiffCountryStatus";

    public static final String REDIS_KEY = "global_covid_";

    public static final String FILENAME = "world";

    private static String TODAY_DATE = TimeUtil.formForeignTodayDate("YYYY-MM-DD",null);

    private static String YESTERDAY_DATE = TimeUtil.formForeignTodayDate("YYYY-MM-DD",1l);

    /** CDC WORLD Data URL */
    @Value("${CDC.WORLD.COVID}")
    public String CDC_WORLD_URL;

    @Resource
    DiffCountryServiceImpl diffCountryService;

    /**
     * 解析各國疫情狀況：<br>
     * iso_code: 1,<br>
     * 國家: 3,<br>
     * 日期: 4,<br>
     * 總確診數 5,<br>
     * 新增確診數: 6,<br>
     * 總死亡數: 8,<br>
     * 新增死亡數: 9,<br>
     * 每百萬人確診數: 11,<br>
     * 每百萬人死亡數: 12,<br>
     * 疫苗總接種人數: 21,<br>
     * 每百人接種疫苗人數: 25<br>
     *
     * @param body CDC的csv檔
     * */
    public void parseCsvInfo(String body){
        //每一行 以換行\n區分
        String[] countries = body.split("\n");
        CopyOnWriteArrayList<String> column = new CopyOnWriteArrayList<>();
        IntStream.range(0,countries.length).parallel().forEach(x -> {
            //每一個 以逗號區分
            Arrays.stream(countries[x].split(",")).forEach(column::add);
            System.out.println(column);
            if(column.size()>=4){
                if(checkTodayTime(column.get(4))){
                    //儲存今日資料
                    saveToDb(column,TODAY_DATE);
                } else if(checkYesterdayTime(column.get(4))){
                    //更新昨日資料
                    updateDb(column,YESTERDAY_DATE);
                }
            }
            column.clear();
        });

    }

    /**
     * 檢查是否為今日日期
     * @param time 今日日期
     * */
    private Boolean checkTodayTime(@NonNull String time){
        return (time.equals(TODAY_DATE))?true:false;
    }

    /**
     * 檢查是否為昨日日期
     * @param time 昨日日期
     * */
    private Boolean checkYesterdayTime(@NonNull String time){
        return (time.equals(YESTERDAY_DATE))?true:false;
    }

    /**
     * 解析column並存至db
     * @param column CopyOnWriteArrayList
     * @param time 日期(當天)
     * */
    private void saveToDb(CopyOnWriteArrayList<String> column,String time){
        DiffCountry diffCountry = genDiffCountry(column,time);
        DiffCountry result = diffCountryService.save(diffCountry);
        if(Objects.isNull(result)){
            log.warn("{} 成功解析各國疫情狀況，但新增db失敗",LOG_PREFIX);
        }
    }

    /**
     * 更新至db，查詢若無則新增
     * @param column CopyOnWriteArrayList
     * @param time 日期(昨日)
     * */
    private void updateDb(CopyOnWriteArrayList<String> column,String time){
        DiffCountry oldCountry = diffCountryService.findByIsoCode(column.get(1));
        if(Objects.isNull(oldCountry)){
            //新增
            saveToDb(column,time);
        } else {
            //更新
            DiffCountry newCountry = genDiffCountry(column,time);
            DiffCountry result = diffCountryService.save(newCountry);
            if(Objects.isNull(result)){
                log.warn("{} 成功解析各國疫情狀況，但更新db失敗",LOG_PREFIX);
            }
        }
    }

    /**
     * 產生DiffCountry
     * @param column
     * @param date
     * */
    private DiffCountry genDiffCountry(CopyOnWriteArrayList<String> column,String date){
        String isoCode = column.get(1);
        String countryName = column.get(3);
        String totalAmount = column.get(5);
        String newAmount = column.get(6);
        String totalDeath = column.get(8);
        String newDeath = column.get(9);
        String confirmedInMillions = column.get(11);
        String deathInMillions = column.get(12);
        String vaccinatedInMillions = column.get(21);
        String vaccinatedInHundreds = column.get(25);
        DiffCountry diffCountry = DiffCountry.builder()
                .isoCode(isoCode)
                .country(countryName)
                .totalAmount(totalAmount)
                .newAmount(newAmount)
                .totalDeath(totalDeath)
                .newDeath(newDeath)
                .confirmedInMillions(confirmedInMillions)
                .deathInMillions(deathInMillions)
                .vaccinatedInMillions(vaccinatedInMillions)
                .vaccinatedInHundreds(vaccinatedInHundreds)
                .lastUpdate(date)
                .build();
        return diffCountry;
    }

}
