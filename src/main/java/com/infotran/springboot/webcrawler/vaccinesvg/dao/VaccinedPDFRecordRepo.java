package com.infotran.springboot.webcrawler.vaccinesvg.dao;

import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccinedPDFRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * VaccinedPDFRecordRepo
 * @author chris
 */
public interface VaccinedPDFRecordRepo extends JpaRepository<VaccinedPDFRecord, Integer>, CrudRepository<VaccinedPDFRecord, Integer> {

    /**
     * 依上傳時間搜尋 VaccinedPDFRecord
     * @param uploadTime 上傳時間
     * @return VaccinedPDFRecord
     * */
    VaccinedPDFRecord findByUploadTime(String uploadTime);
}
