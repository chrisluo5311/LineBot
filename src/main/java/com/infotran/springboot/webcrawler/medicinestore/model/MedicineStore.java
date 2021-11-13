package com.infotran.springboot.webcrawler.medicinestore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import javax.persistence.*;

/**
 * 藥局店家資訊
 * @author chris
 * 
 */
@Entity
@Table(name = "MedicineStore")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class MedicineStore {

    /**
     * 藥局表id
     */
    @Id
    private String id;

    /**
     * 藥局名稱
     * */
    private String name;

    /**
    * 藥局電話
    * */
    private String phoneNumber;

    /**
     * 藥局地址
     * */
    private String address;

    /**
     * 藥局成人口罩剩餘
     * */
    private Integer maskAdult;

    /**
     * 藥局小孩口罩剩餘
     * */
    private Integer maskChild;

    /**
     * 藥局緯度
     * */
    private Double latitude;

    /**
     * 藥局經度
     * */
    private Double longitude;

    /**
     * 更新時間
     * */
    private String updateTime;

}
