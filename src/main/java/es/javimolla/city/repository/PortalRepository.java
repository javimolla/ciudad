package es.javimolla.city.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.javimolla.city.entity.Portal;

@Repository
@Transactional
public interface PortalRepository extends JpaRepository<Portal, Integer> {
	public List<Portal> findByIsochroneIsNull();
}