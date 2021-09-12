package com.infotran.springboot.confirmcase.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.infotran.springboot.confirmcase.model.ConfirmCase;

import java.time.LocalDate;

public interface ConfirmCaseRepository extends JpaRepository<ConfirmCase, Integer> {

    public ConfirmCase findByConfirmTime(LocalDate localDate);


}
