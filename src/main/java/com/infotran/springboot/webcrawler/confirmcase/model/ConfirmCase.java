package com.infotran.springboot.webcrawler.confirmcase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="ConfirmCase")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ConfirmCase {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer confirmId; //確診表id

	private Integer totalAmount; //確診總數
	
	private Integer deathAmount; //死亡數目
	
	private Integer todayAmount; //今日確診數目
	
	private Integer returnAmount; //校正回歸數
	
	private Integer releasedQuarantine; //解除14天隔離數目(沒用到)
	
	private LocalDate confirmTime;//更新時間(now)

	private String newsUrl;//新聞網址
	
	@PrePersist
	protected void createConfirmTime() {
		confirmTime = LocalDate.now();
	}
	
	@PreUpdate
	protected void updateConfirmTime() {
		confirmTime = LocalDate.now();
	}
	
	
}
