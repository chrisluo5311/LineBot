package com.infotran.springboot.MedicineStore.Dao;

import com.infotran.springboot.MedicineStore.Model.MedicineStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface MedicineStoreRepository extends JpaRepository<MedicineStore, Integer> {

    public MedicineStore findByLatitudeAndLongitude(Double latitude, Double longitude);

    public MedicineStore findById (Long id);

}
