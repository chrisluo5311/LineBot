package com.infotran.springboot.ConfirmCase.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.infotran.springboot.ConfirmCase.model.ConfirmCase;

public interface ConfirmCaseRepository extends JpaRepository<ConfirmCase, Integer> {

}
