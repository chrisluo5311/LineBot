package com.infotran.springboot.webcrawler.vaccinesvg.service;

import org.springframework.stereotype.Service;

@Service
public interface CheckPDFRecordSevice {

    public String findByUploadTime(String uploadTime);
}
