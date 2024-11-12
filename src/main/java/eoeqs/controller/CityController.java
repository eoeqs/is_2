package eoeqs.controller;

import eoeqs.model.City;
import eoeqs.model.Coordinates;
import eoeqs.model.Human;
import eoeqs.model.User;
import eoeqs.repository.CoordinatesRepository;
import eoeqs.repository.HumanRepository;
import eoeqs.repository.UserRepository;
import eoeqs.service.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cities")
public class CityController {
    private final CoordinatesRepository coordinatesRepository;
    private final HumanRepository humanRepository;
    private static final Logger logger = LoggerFactory.getLogger(CityController.class);
    private final UserRepository userRepository;
    private final CityService cityService;

    public CityController(CoordinatesRepository coordinatesRepository, HumanRepository humanRepository, UserRepository userRepository, CityService cityService) {
        this.coordinatesRepository = coordinatesRepository;
        this.humanRepository = humanRepository;
        this.userRepository = userRepository;
        this.cityService = cityService;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        throw new UsernameNotFoundException("User not authenticated");
    }

    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody City city) {
        User user = getAuthenticatedUser();
        logger.info("Creating city for user: {}", user.getUsername());

        Coordinates coordinates = coordinatesRepository.findById(city.getCoordinates().getId())
                .orElseThrow(() -> new UsernameNotFoundException("Coordinates not found"));
        city.setCoordinates(coordinates);

        Human human = humanRepository.findById(city.getGovernor().getId())
                .orElseThrow(() -> new UsernameNotFoundException("Governor not found"));
        city.setGovernor(human);

        city.setUser(user);
        City createdCity = cityService.createCity(city);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<City>> getCityById(@PathVariable Long id) {
        logger.info("Fetching city with ID: {}", id);
        User user = getAuthenticatedUser();

        Optional<City> city = cityService.getCityById(id);

        return ResponseEntity.ok(city);
    }

    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Long id, @RequestBody City city) {
        User user = getAuthenticatedUser();
        logger.info("Updating city with ID: {} for user: {}", id, user.getUsername());

        City updatedCity = cityService.updateCity(id, city);
        return ResponseEntity.ok(updatedCity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        logger.info("Deleting city with ID: {} for user: {}", id, user.getUsername());

        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<City>> getAllCities() {
        User user = getAuthenticatedUser();
        logger.info("Fetching all cities for user: {}", user.getUsername());

        List<City> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }
}
