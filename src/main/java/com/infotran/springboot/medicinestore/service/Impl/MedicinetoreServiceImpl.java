package com.infotran.springboot.medicinestore.service.Impl;

import com.infotran.springboot.medicinestore.dao.MedicineStoreRepository;
import com.infotran.springboot.medicinestore.model.MedicineStore;
import com.infotran.springboot.medicinestore.service.MedicineStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicinetoreServiceImpl implements MedicineStoreService {

    @Autowired
    MedicineStoreRepository medRepo;

    @Override
    public MedicineStore findByLatitudeAndLogitude(Double latitude, Double longitude) {
        medRepo.findByLatitudeAndLongitude(latitude,longitude);
        return null;
    }

    @Override
    public MedicineStore save(MedicineStore medicineStore) {
        return medRepo.save(medicineStore);
    }

    @Override
    public List<MedicineStore> findAll() {
        return medRepo.findAll();
    }

    @Override
    public MedicineStore findById (String id){
        return medRepo.findById(id);
    }
}
