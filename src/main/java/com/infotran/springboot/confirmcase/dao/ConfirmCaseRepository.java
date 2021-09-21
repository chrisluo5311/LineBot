package com.infotran.springboot.confirmcase.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.infotran.springboot.confirmcase.model.ConfirmCase;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface ConfirmCaseRepository extends JpaRepository<ConfirmCase, Integer>, CrudRepository<ConfirmCase, Integer> {

    public ConfirmCase findByConfirmTime(LocalDate localDate);


}
