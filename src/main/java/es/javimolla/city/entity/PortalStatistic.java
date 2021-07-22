package es.javimolla.city.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = EntityConstants.PORTAL_STATISTICS)
@Getter
@Setter
public class PortalStatistic {	
	@Id
	private PortalStatisticPK id;
	@Column(name = "equipments")
	private Integer equipments;
}
