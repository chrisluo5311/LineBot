package com.infotran.springboot.webcrawler.multicountry.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
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

    /** 國家 */
    private String country;

    /** 確診數 */
    private String confirmed;

    /** 死亡數 */
    private String deaths;

    /** 每十萬人確診數 */
    private String incidentRate;

    /** 死亡比例: (登記死亡數/確診數) */
    private String fatalityRatio;

    /** 最後更新時間 */
    private String lastUpdate;
}
