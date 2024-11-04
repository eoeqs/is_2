package eoeqs.dao;

import eoeqs.model.Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordinatesDAO extends JpaRepository<Coordinates, Long> {
}
