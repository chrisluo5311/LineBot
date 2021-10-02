package com.infotran.springboot.webcrawler.confirmcase.dao;

import com.infotran.springboot.webcrawler.confirmcase.model.ConfirmCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface ConfirmCaseRepository extends JpaRepository<ConfirmCase, Integer>, CrudRepository<ConfirmCase, Integer> {

    public ConfirmCase findByConfirmTime(LocalDate localDate);


}