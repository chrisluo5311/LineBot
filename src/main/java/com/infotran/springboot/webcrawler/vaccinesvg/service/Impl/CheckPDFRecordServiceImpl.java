package com.infotran.springboot.webcrawler.vaccinesvg.service.Impl;

import com.infotran.springboot.webcrawler.vaccinesvg.dao.VaccinedPDFRecordRepo;
import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccinedPDFRecord;
import com.infotran.springboot.webcrawler.vaccinesvg.service.CheckPDFRecordSevice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * CheckPDFRecordSevice 實作類
 * @author chris
 */
@Slf4j
@Service
public class CheckPDFRecordServiceImpl implements CheckPDFRecordSevice {

    private static final String LOG_PREFIX = "CheckPDFRecordServiceImpl";

    public static final String ISNEWPDF = "NEW";

    @Resource
    VaccinedPDFRecordRepo vaccinedPDFRecordRepo;

    /**
     * 搜尋是否為新的PDF檔案
     * @param uploadTime pdf上傳時間
     * @return null為平台pdf檔為更新檔
     * */
    @Override
    public String findByUploadTime(String uploadTime) {
        VaccinedPDFRecord vaccinedPDFRecord = vaccinedPDFRecordRepo.findByUploadTime(uploadTime);
        if(Objects.nonNull(vaccinedPDFRecord)){
            //代表重複 政府尚未更新pdf
            String time = vaccinedPDFRecord.getUploadTime();
            log.info("政府尚未更新pdf 上次上傳時間為: {}",time);
        } else {
            return ISNEWPDF;
        }
        return null;
    }

    @Override
    public VaccinedPDFRecord save(VaccinedPDFRecord vaccinedPDFRecord) {
        vaccinedPDFRecordRepo.deleteAll();
        return vaccinedPDFRecordRepo.save(vaccinedPDFRecord);
    }

}
