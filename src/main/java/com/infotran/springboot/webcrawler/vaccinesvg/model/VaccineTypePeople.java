package com.infotran.springboot.webcrawler.vaccinesvg.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Table(name = "VaccineTypePeople")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class VaccineTypePeople {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    private String AzAmount;//AZ疫苗累计接踵人次

    private String ModernaAmount;//Moderna疫苗累计接踵人次

    private String MvcAmount;//高端疫苗累计接踵人次

    private String BntAmount;//BNT疫苗累计接踵人次

}
