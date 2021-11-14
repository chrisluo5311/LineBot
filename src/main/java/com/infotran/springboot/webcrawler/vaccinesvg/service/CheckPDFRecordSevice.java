package com.infotran.springboot.webcrawler.vaccinesvg.service;

import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccinedPDFRecord;
import org.springframework.stereotype.Service;

/**
 * CheckPDFRecordSevice
 * @author chris
 */
@Service
public interface CheckPDFRecordSevice {

    String findByUploadTime(String uploadTime);

    VaccinedPDFRecord save(VaccinedPDFRecord vaccinedPDFRecord);
}
