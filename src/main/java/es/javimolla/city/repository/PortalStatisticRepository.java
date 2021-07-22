package es.javimolla.city.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.javimolla.city.entity.PortalStatistic;

public interface PortalStatisticRepository extends JpaRepository<PortalStatistic, Integer>{
	public void deleteByIdGid(Integer gid);
}
