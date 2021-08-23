package com.infotran.springboot.ConfirmCase.service;

import com.infotran.springboot.ConfirmCase.model.ConfirmCase;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ConfirmCaseService {

	ConfirmCase save(ConfirmCase fcase);

	ConfirmCase findByConfirmTime(LocalDate localDate);

}
