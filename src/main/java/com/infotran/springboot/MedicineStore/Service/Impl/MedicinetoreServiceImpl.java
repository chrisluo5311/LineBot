package com.infotran.springboot.MedicineStore.Service.Impl;

import com.infotran.springboot.MedicineStore.Dao.MedicineStoreRepository;
import com.infotran.springboot.MedicineStore.Model.MedicineStore;
import com.infotran.springboot.MedicineStore.Service.MedicineStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    public MedicineStore findById (Long id){
        return medRepo.findById(id);
    }
}
