package com.infotran.springboot.webcrawler.medicinestore.dao;

import com.infotran.springboot.webcrawler.medicinestore.model.MedicineStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicineStoreRepository extends JpaRepository<MedicineStore, String> {

    public MedicineStore findByLatitudeAndLongitude(Double latitude, Double longitude);

    public Optional<MedicineStore> findById (String id);




}
