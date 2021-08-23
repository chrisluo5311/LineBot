package com.infotran.springboot.ConfirmCase.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.infotran.springboot.ConfirmCase.model.ConfirmCase;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ConfirmCaseRepository extends JpaRepository<ConfirmCase, Integer> {

    public ConfirmCase findByConfirmTime(LocalDate localDate);


}
