package com.infotran.springboot.confirmcase.model;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	
	@PrePersist
	protected void createConfirmTime() {
		confirmTime = LocalDate.now();
	}
	
	@PreUpdate
	protected void updateConfirmTime() {
		confirmTime = LocalDate.now();
	}
	
	
}
