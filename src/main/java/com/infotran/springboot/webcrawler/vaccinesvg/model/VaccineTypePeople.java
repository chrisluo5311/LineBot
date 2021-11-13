package com.infotran.springboot.webcrawler.vaccinesvg.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * 各疫苗接踵累计人次
 * @author chris
 */
@Entity
@Table(name = "VaccineTypePeople")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class VaccineTypePeople {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    /** 內文 */
    private String body;

    /** 資料來源 */
    private String resourceUrl;

    /** 新建時間 */
    private String createTime;

}
