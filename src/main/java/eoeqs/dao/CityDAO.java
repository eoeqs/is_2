package eoeqs.dao;

import eoeqs.model.City;
import eoeqs.model.Climate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityDAO extends JpaRepository<City, Long> {
    List<City> findByNameContaining(String name);

    List<City> findByClimateLessThan(Climate climate);

    List<City> findByClimateGreaterThan(Climate climate);
}