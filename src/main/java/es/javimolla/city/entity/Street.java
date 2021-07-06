package es.javimolla.city.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = EntityConstants.STREETS)
@Getter
@Setter
public class Street extends EntityGid implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "street_id")
	private Integer streetId;
	@Column(name = "street_name")
	private String name;
}
