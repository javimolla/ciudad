package es.javimolla.city.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = EntityConstants.PORTALS_ISOCHRONES)
@Getter
@Setter
public class PortalIsochrone implements Serializable {
	public PortalIsochrone() {
	}
	
	public PortalIsochrone(Portal portal, Isochrone isochrone) {
		id = new PortalIsochronePK(portal.getGid(), isochrone.getGid());
	}

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private PortalIsochronePK id;
}
