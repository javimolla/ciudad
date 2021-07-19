package es.javimolla.city.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import es.javimolla.city.entity.EntityConstants;
import es.javimolla.city.entity.Isochrone;
import es.javimolla.city.entity.Portal;
import es.javimolla.city.exception.IsochroneExistsException;

@Repository
public class IsochroneRepository {
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private IsochroneJpaRepository isochroneJpaRepository;

	public Isochrone findByPortal(Portal portal) throws IsochroneExistsException {
		Integer vertexId = getClosestVertexId(portal.getGeom());

		Optional<Isochrone> isochrone = isochroneJpaRepository.findById(vertexId);
		if (isochrone.isPresent()) {
			throw new IsochroneExistsException();
		}

		return (Isochrone) entityManager
				.createNativeQuery(String.format("SELECT %s as gid, get_isochrone(:vertexId) as the_geom", vertexId),
						Isochrone.class)
				.setParameter("vertexId", vertexId).setMaxResults(1).getSingleResult();
	}

	public List<Isochrone> findByStatsIsNull() {
		return entityManager.createQuery(
				"select e from Isochrone e where gid not in (select distinct id.gid from IsochroneStatistic)",
				Isochrone.class).setMaxResults(100).getResultList();
	}
	
	public void save(Isochrone isochrone) {
		isochroneJpaRepository.save(isochrone);
	}

	private Integer getClosestVertexId(Geometry<C2D> geometry) {
		return ((BigInteger) entityManager
				.createNativeQuery(
						String.format("SELECT id FROM %s ORDER BY st_distance(the_geom, st_transform(:geometry, 4326))",
								EntityConstants.VERTEX))
				.setParameter("geometry", geometry).setMaxResults(1).getSingleResult()).intValue();
	}
}
