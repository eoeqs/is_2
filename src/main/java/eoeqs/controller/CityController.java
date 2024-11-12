package eoeqs.controller;

import eoeqs.model.*;
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

import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/filter-by-climate")
    public ResponseEntity<List<City>> getCitiesWithClimateLessThan(@RequestParam Climate climate,
                                                                   @RequestParam String filterType,
                                                                   @RequestHeader("Authorization") String token) {


        {
            User user = getAuthenticatedUser();
            logger.info("Getting cities with climate less or greater than smth for user: {}", user.getUsername());
            List<City> filteredCities;

            if ("less".equals(filterType)) {
                filteredCities = cityService.getCitiesWithClimateLessThan(climate);
            } else if ("more".equals(filterType)) {
                filteredCities = cityService.getCitiesWithClimateGreaterThan(climate);
            } else {
                return ResponseEntity.badRequest().body(null);
            }

            return ResponseEntity.ok(filteredCities);
        }
    }
    @GetMapping("/unique-agglomerations")
    public ResponseEntity<List<Long>> getUniqueAgglomerations(@RequestHeader("Authorization") String token) {
        User user = getAuthenticatedUser();
        logger.info("Getting cities with unique agglomerations for user: {}", user.getUsername());

        try {
            List<Long> uniqueAgglomerations = cityService.getUniqueAgglomerations();
            return ResponseEntity.ok(uniqueAgglomerations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/distance-to-largest-city")
    public ResponseEntity<Double> getDistanceToLargestCity() {
        User user = getAuthenticatedUser();
        logger.info("Getting distance to largest city for user: {}", user.getUsername());

        try {
            double distance = cityService.calculateDistanceToLargestCity();
            return ResponseEntity.ok(distance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }
}
