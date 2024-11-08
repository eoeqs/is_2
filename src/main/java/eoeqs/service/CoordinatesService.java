package eoeqs.service;

import eoeqs.model.Coordinates;
import eoeqs.repository.CoordinatesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoordinatesService {

    private final CoordinatesRepository coordinatesRepository;

    public CoordinatesService(CoordinatesRepository coordinatesRepository) {
        this.coordinatesRepository = coordinatesRepository;
    }

    public Coordinates createCoordinates(Coordinates coordinates) {
        return coordinatesRepository.save(coordinates);
    }

    public Optional<Coordinates> getCoordinatesById(Long id) {
        return coordinatesRepository.findById(id);
    }

    public Coordinates updateCoordinates(Long id, Coordinates coordinates) {
        return coordinatesRepository.findById(id)
                .map(existingCoordinates -> {
                    existingCoordinates.setX(coordinates.getX());
                    existingCoordinates.setY(coordinates.getY());
                    return coordinatesRepository.save(existingCoordinates);
                }).orElseThrow(() -> new RuntimeException("Coordinates not found"));
    }

    public void deleteCoordinates(Long id) {
        coordinatesRepository.deleteById(id);
    }

    public List<Coordinates> getAllCoordinates() {
        return coordinatesRepository.findAll();
    }
}
