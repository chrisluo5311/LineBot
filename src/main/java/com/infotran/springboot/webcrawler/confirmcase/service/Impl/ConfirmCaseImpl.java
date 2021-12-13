package com.infotran.springboot.webcrawler.confirmcase.service.Impl;

import com.infotran.springboot.webcrawler.confirmcase.dao.ConfirmCaseRepository;
import com.infotran.springboot.webcrawler.confirmcase.model.ConfirmCase;
import com.infotran.springboot.webcrawler.confirmcase.service.ConfirmCaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Objects;

/**
 * ConfirmCaseService 實作類
 * @author chris
 */
@Service
public class ConfirmCaseImpl implements ConfirmCaseService {
	
	@Resource
	private ConfirmCaseRepository conRepo;

	@Override
	public ConfirmCase save (ConfirmCase newConfirmCase) {
		ConfirmCase confirmCase = findByConfirmTime(LocalDate.now());
		if (Objects.nonNull(confirmCase)){
			conRepo.delete(confirmCase);
		}
		return conRepo.save(newConfirmCase);
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
