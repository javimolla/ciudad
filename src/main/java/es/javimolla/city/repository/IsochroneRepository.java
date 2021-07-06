package es.javimolla.city.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import es.javimolla.city.entity.Portal;

@Repository
public class IsochroneRepository {
	@PersistenceContext
	private EntityManager entityManager;

	public void calculate(Portal portal) {
		entityManager.createNativeQuery("select calculate_isochrones(:geom, :gid)")
				.setParameter("geom", portal.getGeom()).setParameter("gid", portal.getGid()).getResultList();
	}
}
