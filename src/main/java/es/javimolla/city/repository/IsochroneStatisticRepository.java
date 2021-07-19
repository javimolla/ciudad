package es.javimolla.city.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.javimolla.city.entity.IsochroneStatistic;

public interface IsochroneStatisticRepository extends JpaRepository<IsochroneStatistic, Integer>{
	public void deleteByIdGid(Integer gid);
}
