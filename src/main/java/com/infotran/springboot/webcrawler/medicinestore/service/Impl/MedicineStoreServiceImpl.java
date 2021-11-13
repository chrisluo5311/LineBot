package com.infotran.springboot.webcrawler.medicinestore.service.Impl;

import com.infotran.springboot.webcrawler.medicinestore.dao.MedicineStoreRepository;
import com.infotran.springboot.webcrawler.medicinestore.model.MedicineStore;
import com.infotran.springboot.webcrawler.medicinestore.service.MedicineStoreService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * MedicineStoreService 實作類
 * @author chris
 */
@Service
public class MedicineStoreServiceImpl implements MedicineStoreService {

    @Resource
    MedicineStoreRepository medRepo;

    @Override
    public MedicineStore findByLatitudeAndLogitude(Double latitude, Double longitude) {
        return medRepo.findByLatitudeAndLongitude(latitude,longitude);
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
        Optional<MedicineStore> med = medRepo.findById(id);
        if(!med.isEmpty()){
            return med.get();
        }
        return null;
    }

    @Override
    public List<MedicineStore> saveAll(List<MedicineStore> storeList) {
        List<MedicineStore> list = medRepo.saveAll(storeList);
        return (list!=null)?list:null;
    }
}
