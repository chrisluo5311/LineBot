package com.infotran.springboot.webcrawler.medicinestore.service;

import com.infotran.springboot.webcrawler.medicinestore.model.MedicineStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 藥局店家service
 * @author chris
 */
@Service
public interface MedicineStoreService {

    /**
     * 依經緯度查詢店家
     * @param latitude Double
     * @param longitude Double
     * @return MedicineStore MedicineStore
     * */
    MedicineStore findByLatitudeAndLogitude(Double latitude,Double longitude);

    /**
     * save MedicineStore
     * @param medicineStore MedicineStore
     * @return MedicineStore MedicineStore
     * */
    MedicineStore save(MedicineStore medicineStore);

    /**
     * find all MedicineStore
     * @return List<MedicineStore>
     * */
    List<MedicineStore> findAll();

    /**
     * 依 id 搜尋 MedicineStore
     * @param id MedicineStore id
     * @return  MedicineStore
     * */
    MedicineStore findById (String id);

    /**
     * save all MedicineStore
     * @param storeList List<MedicineStore>
     * @return List<MedicineStore>
     * */
    List<MedicineStore> saveAll(List<MedicineStore> storeList);
}
