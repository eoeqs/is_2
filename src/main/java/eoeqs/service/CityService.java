package eoeqs.service;

import eoeqs.model.City;
import eoeqs.model.Climate;
import eoeqs.model.Coordinates;
import eoeqs.repository.CityRepository;
import eoeqs.repository.HumanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityService {

    private static final Logger logger = LoggerFactory.getLogger(CityService.class);

    private final CityRepository cityRepository;
    private final HumanRepository humanRepository;


    public CityService(CityRepository cityRepository, HumanRepository humanRepository) {
        this.cityRepository = cityRepository;
        this.humanRepository = humanRepository;
    }



    public City createCity(City city) {
        logger.info("Creating city with data: {}", city);
        if (city.getGovernor() != null && city.getGovernor().getId() == null) {
            logger.info("Governor data is new, saving separately: {}", city.getGovernor());
            city.setGovernor(humanRepository.save(city.getGovernor()));
            logger.info("Saved Governor with ID: {}", city.getGovernor().getId());
        }
        City savedCity = cityRepository.save(city);

        logger.info("City created with ID: {}", savedCity.getId());
        return savedCity;
    }

    public Optional<City> getCityById(Long id) {
        logger.info("Fetching city by ID: {}", id);
        return cityRepository.findById(id);
    }

    public City updateCity(Long id, City city) {
        logger.info("Updating city with ID: {} and data: {}", id, city);
        return cityRepository.findById(id)
                .map(existingCity -> {
                    if (city.getName() != null) existingCity.setName(city.getName());
                    if (city.getCoordinates() != null) existingCity.setCoordinates(city.getCoordinates());
                    if (city.getArea() > 0) existingCity.setArea(city.getArea());
                    if (city.getPopulation() != null && city.getPopulation() > 0)
                        existingCity.setPopulation(city.getPopulation());
                    if (city.getCapital() != null) existingCity.setCapital(city.getCapital());
                    if (city.getMetersAboveSeaLevel() > 0)
                        existingCity.setMetersAboveSeaLevel(city.getMetersAboveSeaLevel());
                    if (city.getCarCode() != null) existingCity.setCarCode(city.getCarCode());
                    if (city.getAgglomeration() > 0) existingCity.setAgglomeration(city.getAgglomeration());
                    if (city.getClimate() != null) existingCity.setClimate(city.getClimate());
                    if (city.getGovernor() != null) existingCity.setGovernor(city.getGovernor());
                    logger.info("City updated: {}", existingCity);
                    return cityRepository.save(existingCity);
                }).orElseThrow(() -> new RuntimeException("City not found"));
    }

    public void deleteCity(Long id) {
        logger.info("Deleting city with ID: {}", id);
        cityRepository.deleteById(id);
    }

    public List<City> getAllCities() {
        logger.info("Fetching all cities from database");
        return cityRepository.findAll();
    }

    public List<City> getCitiesWithClimateLessThan(Climate climate) {
        return cityRepository.findAll().stream()
                .filter(city -> city.getClimate().ordinal() < climate.ordinal())
                .collect(Collectors.toList());
    }

    public List<City> getCitiesWithClimateGreaterThan(Climate climate) {
        return cityRepository.findAll().stream()
                .filter(city -> city.getClimate().ordinal() > climate.ordinal())
                .collect(Collectors.toList());
    }


    public List<Long> getUniqueAgglomerations() {
        return cityRepository.findAll().stream()
                .map(City::getAgglomeration)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public void deleteCityWithReassignment(Long cityId, City reassignToCity) {
        if (cityId.equals(reassignToCity.getId())) {
            throw new IllegalArgumentException("The city to delete and the reassignment target cannot be the same");
        }
        logger.info("Reassigning dependencies from city ID: {} to city ID: {}", cityId, reassignToCity.getId());

        City cityToDelete = cityRepository.findById(cityId)
                .orElseThrow(() -> new RuntimeException("City not found"));

        List<City> cities = cityRepository.findAll();
        for (City city : cities) {
            if (city.getCoordinates().equals(cityToDelete.getCoordinates())) {
                city.setCoordinates(reassignToCity.getCoordinates());
            }
            if (city.getGovernor().equals(cityToDelete.getGovernor())) {
                city.setGovernor(reassignToCity.getGovernor());
            }
            cityRepository.save(city);
        }

        cityRepository.delete(cityToDelete);
    }

    public double calculateDistanceToLargestCity() {
        City largestCity = cityRepository.findAll().stream()
                .max(Comparator.comparing(City::getArea))
                .orElseThrow(() -> new RuntimeException("No cities found"));

        Coordinates coordinates = largestCity.getCoordinates();
        Float x = coordinates.getX();
        double y = coordinates.getY();

        if (x == null || x <= -586) {
            throw new IllegalArgumentException("Invalid coordinates: x must be greater than -586 and cannot be null");
        }


        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public List<City> getCitiesByUserId(Long userId) {
        return cityRepository.findByUserId(userId);
    }

    public Page<City> getPaginatedCities(Pageable pageable) {
        return cityRepository.findAll(pageable);
    }
}
