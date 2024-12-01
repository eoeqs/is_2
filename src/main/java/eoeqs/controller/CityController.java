package eoeqs.controller;

import eoeqs.exception.*;
import eoeqs.model.*;
import eoeqs.repository.CoordinatesRepository;
import eoeqs.repository.HumanRepository;
import eoeqs.repository.OAuthUserRepository;
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
    private final OAuthUserRepository oAuthUserRepository;
    private final CityService cityService;
    private final SimpMessagingTemplate messagingTemplate;


    public CityController(CoordinatesRepository coordinatesRepository, HumanRepository humanRepository, OAuthUserRepository oAuthUserRepository, CityService cityService, SimpMessagingTemplate messagingTemplate) {
        this.coordinatesRepository = coordinatesRepository;
        this.humanRepository = humanRepository;
        this.oAuthUserRepository = oAuthUserRepository;
        this.cityService = cityService;
        this.messagingTemplate = messagingTemplate;
    }


    @PostMapping
    public ResponseEntity<City> createCity(Authentication authentication, @RequestBody City city) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Creating city for user: {}", authentication.getName());


        Coordinates coordinates = coordinatesRepository.findById(city.getCoordinates().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Coordinates not found"));
        city.setCoordinates(coordinates);

        Human human = humanRepository.findById(city.getGovernor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Governor not found"));
        city.setGovernor(human);
        String username = authentication.getName();
        OAuthUser oAuthUser = oAuthUserRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("User is not authorized"));


        city.setUser(oAuthUser);
        City createdCity = cityService.createCity(city);

        messagingTemplate.convertAndSend("/topic/cities", createdCity);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<City>> getCityById(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }

        logger.info("Fetching city with ID: {}", id);

        Optional<City> city = cityService.getCityById(id);

        return ResponseEntity.ok(city);
    }

    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(Authentication authentication, @PathVariable Long id, @RequestBody City city) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Updating city with ID: {} for user: {}", id, authentication.getName());

        City existingCity = cityService.getCityById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));
        String username = authentication.getName();
        OAuthUser oAuthUser = oAuthUserRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("User is not authorized"));

        if (!canModifyCity(oAuthUser, existingCity)) {
            throw new ForbiddenException("You do not have permission to modify this city.");
        }

        City updatedCity = cityService.updateCity(id, city);
        messagingTemplate.convertAndSend("/topic/cities", updatedCity);

        return ResponseEntity.ok(updatedCity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Deleting city with ID: {} for user: {}", id, authentication.getName());
        City cityToDelete = cityService.getCityById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found"));
        String username = authentication.getName();
        OAuthUser oAuthUser = oAuthUserRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("User is not authorized"));

        if (!canModifyCity(oAuthUser, cityToDelete)) {
            throw new ForbiddenException("You do not have permission to delete this city.");
        }
        cityService.deleteCity(id);
        messagingTemplate.convertAndSend("/topic/cities", Map.of("action", "delete", "id", id));

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<City>> getAllCities(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Fetching all cities for user: {}", authentication.getName());

        List<City> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/filter-by-climate")
    public ResponseEntity<List<City>> getCitiesWithClimateLessThan(Authentication authentication, @RequestParam Climate climate,
                                                                   @RequestParam String filterType
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Getting cities with climate less or greater than smth for user: {}", authentication.getName());
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

    @GetMapping("/unique-agglomerations")
    public ResponseEntity<List<Long>> getUniqueAgglomerations(Authentication authentication, @RequestHeader("Authorization") String token) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Getting cities with unique agglomerations for user: {}", authentication.getName());

        try {
            List<Long> uniqueAgglomerations = cityService.getUniqueAgglomerations();
            return ResponseEntity.ok(uniqueAgglomerations);
        } catch (Exception e) {
            throw new InternalServerErrorException("Something went wrong, try again later.");
        }
    }

    @GetMapping("/distance-to-largest-city")
    public ResponseEntity<Double> getDistanceToLargestCity(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Getting distance to largest city for user: {}", authentication.getName());

        try {
            double distance = cityService.calculateDistanceToLargestCity();
            return ResponseEntity.ok(distance);
        } catch (Exception e) {
            throw new InternalServerErrorException("Something went wrong, try again later.");

        }
    }

    @DeleteMapping("/{id}/reassign")
    public ResponseEntity<Void> deleteCityWithReassignment(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam Long reassignToCityId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("Deleting city with ID: {} and reassigning dependencies to city with ID: {}", id, reassignToCityId);

        City cityToDelete = cityService.getCityById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));
        City reassignToCity = cityService.getCityById(reassignToCityId)
                .orElseThrow(() -> new RuntimeException("Target city for reassignment not found"));
        String username = authentication.getName();
        OAuthUser oAuthUser = oAuthUserRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("User is not authorized"));

        if (!canModifyCity(oAuthUser, cityToDelete) || !canModifyCity(oAuthUser, reassignToCity)) {
            throw new ForbiddenException("You do not have access to delete city.");
        }

        cityService.deleteCityWithReassignment(id, reassignToCity);
        messagingTemplate.convertAndSend("/topic/cities", Map.of("action", "delete", "id", id));

        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(OAuthUser oAuthUser) {

        return oAuthUser.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
    }


    private boolean canModifyCity(OAuthUser user, City city) {
        return isAdmin(user) || city.getUser().getId().equals(user.getId());
    }

    @GetMapping("/editable")
    public ResponseEntity<List<City>> getAllEditableCities( Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        logger.info("user: {}", authentication.getName());

        List<City> cities;
        String username = authentication.getName();
        OAuthUser oAuthUser = oAuthUserRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("User is not authorized"));

        if (isAdmin(oAuthUser)) {
            cities = cityService.getAllCities();
        } else {
            cities = cityService.getCitiesByUserId(oAuthUser.getId());
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
