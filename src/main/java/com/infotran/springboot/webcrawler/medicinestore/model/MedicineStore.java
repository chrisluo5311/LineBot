package com.infotran.springboot.webcrawler.medicinestore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import javax.persistence.*;

@Entity
@Table(name = "MedicineStore")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class MedicineStore {
    @Id
    private String id; //藥局表id

    private String name; //藥局名稱

    private String phoneNumber; //藥局電話

    private String address; //藥局地址

    private Integer maskAdult; //藥局成人口罩剩餘

    private Integer maskChild; //藥局小孩口罩剩餘

    private Double latitude; //藥局緯度

    private Double longitude; //藥局經度

    private String updateTime; //更新時間

}
