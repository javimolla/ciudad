package es.javimolla.city.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.javimolla.city.entity.Isochrone;

@Repository
@Transactional
public interface IsochroneJpaRepository extends JpaRepository<Isochrone, Integer> {
}