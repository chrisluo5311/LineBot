package com.infotran.springboot.ConfirmCase.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
	private Integer confirmId;

	private Integer totalAmount;
	
	private Integer deathAmount;
	
	private Integer todayAmount;
	
	private Integer returnAmount;
	
	private Integer releasedQuarantine;
	
	private LocalDate confirmTime;
	
	@PrePersist
	protected void createConfirmTime() {
		confirmTime = LocalDate.now();
	}
	
	@PreUpdate
	protected void updateConfirmTime() {
		confirmTime = LocalDate.now();
	}
	
	
}
