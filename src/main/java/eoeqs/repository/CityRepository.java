package eoeqs.repository;

import eoeqs.model.City;
import eoeqs.model.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    City findByName(String name);
    List<City> findByUserId(Long userId);
    List<City> findByCoordinates(Coordinates coordinates);
    List<Long> findIdsByCoordinates(Coordinates coordinates);
    Optional<City> findByNameAndPopulation(String name, Long population);

}