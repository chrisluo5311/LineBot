package com.infotran.springboot.webcrawler.multicountry.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * CDC各國疫情狀態的csv檔對應欄位的model
 * @author chris
 */
@Entity
@Table(name = "DiffCountry")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class DiffCountry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** iso code */
    private String isoCode;

    /** 國家 */
    private String country;

    /** 確診數 */
    private String totalAmount;

    /** 新增確診數 */
    private String newAmount;

    /** 死亡數 */
    private String totalDeath;

    /** 新增確診數 */
    private String newDeath;

    /** 每百萬人確診數 */
    private String confirmedInMillions;

    /** 每百萬人死亡數 */
    private String deathInMillions;

    /** 疫苗總接種人數 */
    private String totalVaccinated;

    /** 每百人接種疫苗人數 */
    private String vaccinatedInHundreds;

    /** 最後更新時間 */
    private String lastUpdate;
}
