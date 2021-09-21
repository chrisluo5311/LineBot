package com.infotran.springboot.medicinestore.dao;

import com.infotran.springboot.medicinestore.model.MedicineStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicineStoreRepository extends JpaRepository<MedicineStore, String> {

    public MedicineStore findByLatitudeAndLongitude(Double latitude, Double longitude);

    public Optional<MedicineStore> findById (String id);

//    public List<MedicineStore> findAll();
}
