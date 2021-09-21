package com.infotran.springboot.confirmcase.service.Impl;

import com.infotran.springboot.annotation.LogInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.infotran.springboot.confirmcase.dao.ConfirmCaseRepository;
import com.infotran.springboot.confirmcase.model.ConfirmCase;
import com.infotran.springboot.confirmcase.service.ConfirmCaseService;

import java.time.LocalDate;

@Service
public class ConfirmCaseImpl implements ConfirmCaseService {
	
	@Autowired
	private ConfirmCaseRepository conRepo;

	private RedisTemplate confirmCaseRedisTemplate;
	
	@Override
	public ConfirmCase save (ConfirmCase fcase) {
//		ConfirmCase confirmCase = findByConfirmTime(LocalDate.now());
//		// confirmCase不為Null
//		if (confirmCase!=null){
//			return confirmCase;
//		}
		return conRepo.save(fcase);
	}

	@Override
	public ConfirmCase findByConfirmTime(LocalDate localDate) {
//		confirmCaseRedisTemplate.opsForValue().
		return conRepo.findByConfirmTime(localDate);
	}


}
