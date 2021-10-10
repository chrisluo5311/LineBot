package com.infotran.springboot.webcrawler.vaccinesvg.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Table(name = "VaccinedPDFRecord")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class VaccinedPDFRecord {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    private String uploadTime;//政府疫苗統計pdf的上傳日期

}
