package com.infotran.springboot.medicinestore.dao;

import com.infotran.springboot.medicinestore.model.MedicineStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineStoreRepository extends JpaRepository<MedicineStore, Integer> {

    public MedicineStore findByLatitudeAndLongitude(Double latitude, Double longitude);

    public MedicineStore findById (String id);

}
