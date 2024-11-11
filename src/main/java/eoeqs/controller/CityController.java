package eoeqs.controller;

import eoeqs.model.City;
import eoeqs.model.User;
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

    private static final Logger logger = LoggerFactory.getLogger(CityController.class);
    private final UserRepository userRepository;
    private final CityService cityService;

    public CityController(UserRepository userRepository, CityService cityService) {
        this.userRepository = userRepository;
        this.cityService = cityService;
    }

    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody City city) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("receiveed authentication {}", authentication);
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
//            Object principal = authentication.getPrincipal();
//            System.out.println(principal);
//            if (principal instanceof User) {
//                Long userId = ((User) principal).getId();
//                System.out.println("User ID: " + userId);
//            }
            String username = userDetails.getUsername();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            city.setUser(user);
            logger.info("Received city data for creation: {}", city);
            City createdCity = cityService.createCity(city);
            logger.info("Created city: {}", createdCity);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<City>> getCityById(@PathVariable Long id) {
        logger.info("Fetching city with ID: {}", id);
        Optional<City> city = cityService.getCityById(id);
        return ResponseEntity.ok(city);
    }

    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Long id, @RequestBody City city) {
        logger.info("Updating city with ID: {}, Data: {}", id, city);
        City updatedCity = cityService.updateCity(id, city);
        logger.info("Updated city: {}", updatedCity);
        return ResponseEntity.ok(updatedCity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        logger.info("Deleting city with ID: {}", id);
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<City>> getAllCities() {
        logger.info("Fetching all cities");
        List<City> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }
}
