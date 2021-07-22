package es.javimolla.city.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import es.javimolla.city.entity.PortalSimple;

@Repository
public class PortalSimpleRepository {
	@PersistenceContext
	private EntityManager entityManager;

	public List<PortalSimple> findByStatsIsNull() {
		return entityManager.createQuery(
				"select e from PortalSimple e where gid not in (select distinct id.gid from PortalStatistic)",
				PortalSimple.class).setMaxResults(100).getResultList();
	}
}