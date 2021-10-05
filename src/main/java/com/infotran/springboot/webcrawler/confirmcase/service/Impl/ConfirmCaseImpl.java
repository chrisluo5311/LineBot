package com.infotran.springboot.webcrawler.confirmcase.service.Impl;

import com.infotran.springboot.webcrawler.confirmcase.dao.ConfirmCaseRepository;
import com.infotran.springboot.webcrawler.confirmcase.model.ConfirmCase;
import com.infotran.springboot.webcrawler.confirmcase.service.ConfirmCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ConfirmCaseImpl implements ConfirmCaseService {
	
	@Autowired
	private ConfirmCaseRepository conRepo;

	@Override
	public ConfirmCase save (ConfirmCase fcase) {
		ConfirmCase cfc = findByConfirmTime(LocalDate.now());
		if (cfc != null){
			conRepo.delete(cfc);
		}
		return conRepo.save(fcase);
	}

	@Override
	public ConfirmCase findByConfirmTime(LocalDate localDate) {
		return conRepo.findByConfirmTime(localDate);
	}

	@Override
	public ConfirmCase update(ConfirmCase fcase) {
		return conRepo.save(fcase);
	}


}
