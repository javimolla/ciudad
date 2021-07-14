package es.javimolla.city.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = EntityConstants.PORTALS)
@Getter
@Setter
public class Portal extends FeatureGeomGid implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "portal_number")
	private Long number;
	
	@JoinTable(name = EntityConstants.PORTALS_ISOCHRONES, joinColumns = {
			@JoinColumn(name = "gid", referencedColumnName = "gid") }, inverseJoinColumns = {
					@JoinColumn(name = "isochrone", referencedColumnName = "gid", unique = true) })
	@ManyToOne(fetch = FetchType.LAZY)
	private Isochrone isochrone;
}
