package com.infotran.springboot.webcrawler.medicinestore.service;

import com.infotran.springboot.webcrawler.medicinestore.model.MedicineStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MedicineStoreService {

    public MedicineStore findByLatitudeAndLogitude(Double latitude,Double longitude);

    public MedicineStore save(MedicineStore medicineStore);

    public List<MedicineStore> findAll();

    public MedicineStore findById (String id);

    public List<MedicineStore> saveAll(List<MedicineStore> storeList);
}
