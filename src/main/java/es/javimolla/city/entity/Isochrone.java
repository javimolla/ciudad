package es.javimolla.city.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = EntityConstants.ISOCHRONES)
public class Isochrone extends FeatureGeomGid implements Serializable {
	private static final long serialVersionUID = 1L;
}
