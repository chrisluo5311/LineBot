package com.infotran.springboot.webcrawler.confirmcase.service;

import com.infotran.springboot.webcrawler.confirmcase.model.ConfirmCase;

import java.time.LocalDate;

public interface ConfirmCaseService {

	ConfirmCase save(ConfirmCase fcase);

	ConfirmCase findByConfirmTime(LocalDate localDate);

	ConfirmCase update(ConfirmCase fcase);

}
