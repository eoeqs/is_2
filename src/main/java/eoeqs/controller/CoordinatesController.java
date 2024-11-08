package eoeqs.controller;

import eoeqs.model.Coordinates;
import eoeqs.service.CoordinatesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/coordinates")
public class CoordinatesController {

    private final CoordinatesService coordinatesService;

    public CoordinatesController(CoordinatesService coordinatesService) {
        this.coordinatesService = coordinatesService;
    }

    @PostMapping
    public ResponseEntity<Coordinates> createCoordinates(@RequestBody Coordinates coordinates) {
        Coordinates createdCoordinates = coordinatesService.createCoordinates(coordinates);
        return ResponseEntity.ok(createdCoordinates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Coordinates>> getCoordinatesById(@PathVariable Long id) {
        Optional<Coordinates> coordinates = coordinatesService.getCoordinatesById(id);
        return ResponseEntity.ok(coordinates);
    }

    @GetMapping
    public ResponseEntity<List<Coordinates>> getAllCoordinates() {
        List<Coordinates> coordinatesList = coordinatesService.getAllCoordinates();
        return ResponseEntity.ok(coordinatesList);
    }
}
