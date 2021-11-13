package com.infotran.springboot.webcrawler.confirmcase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author chris
 */
@Entity
@Table(name="ConfirmCase")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ConfirmCase {

	/** 確診表id */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer confirmId;

	/** 確診總數 */
	private Integer totalAmount;

	/** 死亡數目 */
	private Integer deathAmount;

	/** 今日確診數目 */
	private Integer todayAmount;

	/** 確診案例中分佈(境外移入還是本土) */
	private String domesticOrImportedCaseMemo;

	/** 校正回歸數 */
	private Integer returnAmount;

	/** 更新時間(now) */
	private LocalDate confirmTime;

	/** 新聞網址 */
	private String newsUrl;
	
	@PrePersist
	protected void createConfirmTime() {
		confirmTime = LocalDate.now();
	}
	
	@PreUpdate
	protected void updateConfirmTime() {
		confirmTime = LocalDate.now();
	}
	
	
}
