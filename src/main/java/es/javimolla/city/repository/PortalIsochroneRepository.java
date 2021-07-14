package es.javimolla.city.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.javimolla.city.entity.PortalIsochrone;
import es.javimolla.city.entity.PortalIsochronePK;

@Repository
@Transactional
public interface PortalIsochroneRepository extends JpaRepository<PortalIsochrone, PortalIsochronePK> {
}