package com.infotran.springboot.ConfirmCase.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.infotran.springboot.ConfirmCase.dao.ConfirmCaseRepository;
import com.infotran.springboot.ConfirmCase.model.ConfirmCase;
import com.infotran.springboot.ConfirmCase.service.ConfirmCaseService;
import com.sun.istack.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ConfirmCaseImpl implements ConfirmCaseService {
	
	@Autowired
	private ConfirmCaseRepository conRepo;
	
	@Override
	public ConfirmCase save (ConfirmCase fcase) {
		return conRepo.save(fcase);
	}

	@Override
	public ConfirmCase findByConfirmTime(LocalDate localDate) {
		return conRepo.findByConfirmTime(localDate);
	}

}
