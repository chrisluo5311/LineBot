package com.infotran.springboot.medicinestore.model;

import org.springframework.stereotype.Component;

import javax.annotation.processing.Generated;
import javax.persistence.*;

@Entity
@Table(name = "MedicineStore")
@Component
public class MedicineStore {

    @Id
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    @Override
    public String toString() {
        return "MedicineStore{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", maskAdult=" + maskAdult +
                ", maskChild=" + maskChild +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
