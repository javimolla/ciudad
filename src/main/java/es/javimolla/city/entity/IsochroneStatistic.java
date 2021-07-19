package es.javimolla.city.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = EntityConstants.ISOCHRONES_STATISTICS)
@Getter
@Setter
public class IsochroneStatistic {
	@Id
	private IsochroneStatisticPK id;
	@Column(name = "equipments")
	private Integer equipments;
}
