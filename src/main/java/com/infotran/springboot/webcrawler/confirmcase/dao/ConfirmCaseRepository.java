package com.infotran.springboot.webcrawler.confirmcase.dao;

import com.infotran.springboot.webcrawler.confirmcase.model.ConfirmCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

/**
 * ConfirmCaseRepository
 * @author chris
 */
public interface ConfirmCaseRepository extends JpaRepository<ConfirmCase, Integer>, CrudRepository<ConfirmCase, Integer> {

    /**
     * 依當天日期查詢 ConfirmCase
     * @param localDate 當天日期
     * @return ConfirmCase
     * */
    ConfirmCase findByConfirmTime(LocalDate localDate);


}
