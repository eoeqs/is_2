package eoeqs.controller;

import eoeqs.exception.*;
import eoeqs.model.*;
import eoeqs.repository.CoordinatesRepository;
import eoeqs.repository.HumanRepository;
import eoeqs.repository.UserRepository;
import eoeqs.service.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    private final CoordinatesRepository coordinatesRepository;
    private final HumanRepository humanRepository;
    private static final Logger logger = LoggerFactory.getLogger(CityController.class);
    private final UserRepository userRepository;
    private final CityService cityService;
    private final SimpMessagingTemplate messagingTemplate;


    public CityController(CoordinatesRepository coordinatesRepository, HumanRepository humanRepository, UserRepository userRepository, CityService cityService, SimpMessagingTemplate messagingTemplate) {
        this.coordinatesRepository = coordinatesRepository;
        this.humanRepository = humanRepository;
        this.userRepository = userRepository;
        this.cityService = cityService;
        this.messagingTemplate = messagingTemplate;
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
                .orElseThrow(() -> new ResourceNotFoundException("Coordinates not found"));
        city.setCoordinates(coordinates);

        Human human = humanRepository.findById(city.getGovernor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Governor not found"));
        city.setGovernor(human);

        if (!isAdmin(user)) {
            throw new UnauthorizedAccessException("User is not authorized to create cities.");
        }
        city.setUser(user);
        City createdCity = cityService.createCity(city);

        messagingTemplate.convertAndSend("/topic/cities", createdCity);

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

        City existingCity = cityService.getCityById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));

        if (!canModifyCity(user, existingCity)) {
            throw new ForbiddenException("You do not have permission to modify this city.");
        }

        City updatedCity = cityService.updateCity(id, city);
        messagingTemplate.convertAndSend("/topic/cities", updatedCity);

        return ResponseEntity.ok(updatedCity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        logger.info("Deleting city with ID: {} for user: {}", id, user.getUsername());
        City cityToDelete = cityService.getCityById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found"));

        if (!canModifyCity(user, cityToDelete)) {
            throw new ForbiddenException("You do not have permission to delete this city.");
        }
        cityService.deleteCity(id);
        messagingTemplate.convertAndSend("/topic/cities", Map.of("action", "delete", "id", id));

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
                throw new BadRequestException("Invalid filter type. Use 'less' or 'more'.");
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
            throw new InternalServerErrorException("Something went wrong, try again later.");
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
            throw new InternalServerErrorException("Something went wrong, try again later.");

        }
    }

    @DeleteMapping("/{id}/reassign")
    public ResponseEntity<Void> deleteCityWithReassignment(
            @PathVariable Long id,
            @RequestParam Long reassignToCityId) {
        User user = getAuthenticatedUser();
        logger.info("Deleting city with ID: {} and reassigning dependencies to city with ID: {}", id, reassignToCityId);

        City cityToDelete = cityService.getCityById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));
        City reassignToCity = cityService.getCityById(reassignToCityId)
                .orElseThrow(() -> new RuntimeException("Target city for reassignment not found"));

        if (!canModifyCity(user, cityToDelete) || !canModifyCity(user, reassignToCity)) {
            throw new ForbiddenException("You do not have access to delete city.");
        }

        cityService.deleteCityWithReassignment(id, reassignToCity);
        messagingTemplate.convertAndSend("/topic/cities", Map.of("action", "delete", "id", id));

        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
    }


    private boolean canModifyCity(User user, City city) {
        return isAdmin(user) || city.getUser().getId().equals(user.getId());
    }

    @GetMapping("/editable")
    public ResponseEntity<List<City>> getAllEditableCities() {
        User user = getAuthenticatedUser();
        logger.info("user: {}", user);

        List<City> cities;
        if (isAdmin(user)) {
            cities = cityService.getAllCities();
            logger.info("tut");
        } else {
            cities = cityService.getCitiesByUserId(user.getId());
            logger.info("ne tut");
        }

        return ResponseEntity.ok(cities);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<City>> getPaginatedCities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        logger.info("Fetching paginated cities: page {}, size {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<City> paginatedCities = cityService.getPaginatedCities(pageable);
        return ResponseEntity.ok(paginatedCities);
    }


}
