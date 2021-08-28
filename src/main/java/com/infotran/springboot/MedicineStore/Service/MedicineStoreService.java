package com.infotran.springboot.MedicineStore.Service;

import com.infotran.springboot.MedicineStore.Model.MedicineStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
public interface MedicineStoreService {

    public MedicineStore findByLatitudeAndLogitude(Double latitude,Double longitude);

    public MedicineStore save(MedicineStore medicineStore);

    public List<MedicineStore> findAll();

    public MedicineStore findById (Long id);
}
