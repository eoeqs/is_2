package eoeqs.service;

import eoeqs.dao.CoordinatesDAO;
import eoeqs.model.Coordinates;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoordinatesService {
    private final CoordinatesDAO coordinatesDAO;

    public Coordinates getCoordinatesById(Long id) {
        Optional<Coordinates> coordinates = coordinatesDAO.findById(id);
        if (coordinates.isEmpty()) {
            throw new RuntimeException("Coordinates not found");
        }
        return coordinates.get();
    }

    public Coordinates createCoordinates(Coordinates coordinates) {
        coordinatesDAO.save(coordinates);
        return coordinates;
    }
}