package com.infotran.springboot.confirmcase.service;

import com.infotran.springboot.confirmcase.model.ConfirmCase;

import java.time.LocalDate;

public interface ConfirmCaseService {

	ConfirmCase save(ConfirmCase fcase);

	ConfirmCase findByConfirmTime(LocalDate localDate);

}
