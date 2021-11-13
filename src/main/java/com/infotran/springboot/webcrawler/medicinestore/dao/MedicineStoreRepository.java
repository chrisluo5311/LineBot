package com.infotran.springboot.webcrawler.medicinestore.dao;

import com.infotran.springboot.webcrawler.medicinestore.model.MedicineStore;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * MedicineStoreRepository
 * @author chris
 */
public interface MedicineStoreRepository extends JpaRepository<MedicineStore, String> {

    /**
     * 依經緯度查找MedicineStore
     * @param latitude 緯度
     * @param longitude 經度
     * @return MedicineStore
     *
     * */
     MedicineStore findByLatitudeAndLongitude(Double latitude, Double longitude);

    /**
     * 依id查找MedicineStore
     * @param id MedicineStore的id
     * @return MedicineStore
     *
     * */
    @Nonnull
    @Override
    Optional<MedicineStore> findById (@Nonnull String id);

}
