package com.infotran.springboot.MedicineStore.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MedicineStore")
@Component
public class MedicineStore {

    @Id
    private long id;

    private String name;

    private String phoneNumber;

    private String address;

    private Integer maskAdult;

    private Integer maskChild;

    private Double latitude;

    private Double longitude;

    private String updateTime;

    public MedicineStore() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getMaskAdult() {
        return maskAdult;
    }

    public void setMaskAdult(Integer maskAdult) {
        this.maskAdult = maskAdult;
    }

    public Integer getMaskChild() {
        return maskChild;
    }

    public void setMaskChild(Integer maskChild) {
        this.maskChild = maskChild;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
