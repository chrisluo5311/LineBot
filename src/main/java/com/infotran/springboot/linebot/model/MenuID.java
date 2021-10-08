package com.infotran.springboot.linebot.model;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MenuID")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class MenuID implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;
	
	private String menuId;
	
	private String menuName;
	
}
