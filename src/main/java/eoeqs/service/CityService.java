package eoeqs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import eoeqs.dto.CityHistoryDto;
import eoeqs.exception.InvalidDataException;
import eoeqs.model.*;
import eoeqs.repository.CityRepository;
import eoeqs.repository.CoordinatesRepository;
import eoeqs.repository.HumanRepository;
import eoeqs.repository.ImportHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CityService {

    private static final Logger logger = LoggerFactory.getLogger(CityService.class);

    private final CityRepository cityRepository;
    private final HumanRepository humanRepository;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final CoordinatesRepository coordinatesRepository;
    private final ImportHistoryRepository importHistoryRepository;

    public CityService(CityRepository cityRepository, HumanRepository humanRepository, CoordinatesRepository coordinatesRepository, ImportHistoryRepository importHistoryRepository) {
        this.cityRepository = cityRepository;
        this.humanRepository = humanRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.importHistoryRepository = importHistoryRepository;
    }

    public City createCity(City city) {
        logger.info("Creating city with data: {}", city);
        if (city.getGovernor() != null && city.getGovernor().getId() == null) {
            logger.info("Governor data is new, saving separately: {}", city.getGovernor());
            city.setGovernor(humanRepository.save(city.getGovernor()));
            logger.info("Saved Governor with ID: {}", city.getGovernor().getId());
        }

        if (city.getCoordinates() != null && city.getCoordinates().getId() == null) {
            logger.info("Coordinates are new, saving separately: {}", city.getCoordinates());
            city.setCoordinates(coordinatesRepository.save(city.getCoordinates()));
            logger.info("Saved Coordinates with ID: {}", city.getCoordinates().getId());
        }

        if (city.getGovernor() == null || city.getCoordinates() == null) {
            logger.error("City must have a valid governor and coordinates. Governor: {}, Coordinates: {}", city.getGovernor(), city.getCoordinates());
            throw new InvalidDataException("City must have a valid governor and coordinates.");
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
                    logger.info("Found existing city: {}", existingCity);
                    if (city.getName() != null) {
                        logger.info("Updating name from {} to {}", existingCity.getName(), city.getName());
                        existingCity.setName(city.getName());
                    }
                    if (city.getCoordinates() != null) {
                        logger.info("Updating coordinates from {} to {}", existingCity.getCoordinates(), city.getCoordinates());
                        existingCity.setCoordinates(city.getCoordinates());
                    }
                    if (city.getArea() > 0) {
                        logger.info("Updating area from {} to {}", existingCity.getArea(), city.getArea());
                        existingCity.setArea(city.getArea());
                    }
                    if (city.getPopulation() != null && city.getPopulation() > 0) {
                        logger.info("Updating population from {} to {}", existingCity.getPopulation(), city.getPopulation());
                        existingCity.setPopulation(city.getPopulation());
                    }
                    if (city.getCapital() != null) {
                        logger.info("Updating capital from {} to {}", existingCity.getCapital(), city.getCapital());
                        existingCity.setCapital(city.getCapital());
                    }
                    if (city.getMetersAboveSeaLevel() > 0) {
                        logger.info("Updating metersAboveSeaLevel from {} to {}", existingCity.getMetersAboveSeaLevel(), city.getMetersAboveSeaLevel());
                        existingCity.setMetersAboveSeaLevel(city.getMetersAboveSeaLevel());
                    }
                    if (city.getCarCode() != null) {
                        logger.info("Updating carCode from {} to {}", existingCity.getCarCode(), city.getCarCode());
                        existingCity.setCarCode(city.getCarCode());
                    }
                    if (city.getAgglomeration() > 0) {
                        logger.info("Updating agglomeration from {} to {}", existingCity.getAgglomeration(), city.getAgglomeration());
                        existingCity.setAgglomeration(city.getAgglomeration());
                    }
                    if (city.getClimate() != null) {
                        logger.info("Updating climate from {} to {}", existingCity.getClimate(), city.getClimate());
                        existingCity.setClimate(city.getClimate());
                    }
                    if (city.getGovernor() != null) {
                        logger.info("Updating governor from {} to {}", existingCity.getGovernor(), city.getGovernor());
                        existingCity.setGovernor(city.getGovernor());
                    }
                    logger.info("City updated: {}", existingCity);
                    return cityRepository.save(existingCity);
                }).orElseThrow(() -> {
                    logger.error("City with ID {} not found for update", id);
                    return new RuntimeException("City not found");
                });
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
        logger.info("Fetching cities with climate less than: {}", climate);
        return cityRepository.findAll().stream()
                .filter(city -> city.getClimate().ordinal() < climate.ordinal())
                .collect(Collectors.toList());
    }

    public List<City> getCitiesWithClimateGreaterThan(Climate climate) {
        logger.info("Fetching cities with climate greater than: {}", climate);
        return cityRepository.findAll().stream()
                .filter(city -> city.getClimate().ordinal() > climate.ordinal())
                .collect(Collectors.toList());
    }

    public List<Long> getUniqueAgglomerations() {
        logger.info("Fetching unique agglomerations");
        return cityRepository.findAll().stream()
                .map(City::getAgglomeration)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public void deleteCityWithReassignment(Long cityId, City reassignToCity) {
        if (cityId.equals(reassignToCity.getId())) {
            logger.error("The city to delete and the reassignment target cannot be the same: {}", cityId);
            throw new IllegalArgumentException("The city to delete and the reassignment target cannot be the same");
        }
        logger.info("Reassigning dependencies from city ID: {} to city ID: {}", cityId, reassignToCity.getId());

        City cityToDelete = cityRepository.findById(cityId)
                .orElseThrow(() -> {
                    logger.error("City with ID {} not found for deletion", cityId);
                    return new RuntimeException("City not found");
                });

        List<City> cities = cityRepository.findAll();
        for (City city : cities) {
            if (city.getCoordinates().equals(cityToDelete.getCoordinates())) {
                logger.info("Reassigning coordinates from city ID {} to city ID {}", city.getId(), reassignToCity.getId());
                city.setCoordinates(reassignToCity.getCoordinates());
            }
            if (city.getGovernor().equals(cityToDelete.getGovernor())) {
                logger.info("Reassigning governor from city ID {} to city ID {}", city.getId(), reassignToCity.getId());
                city.setGovernor(reassignToCity.getGovernor());
            }
            cityRepository.save(city);
        }

        cityRepository.delete(cityToDelete);
    }

    public double calculateDistanceToLargestCity() {
        logger.info("Calculating distance to the largest city");
        City largestCity = cityRepository.findAll().stream()
                .max(Comparator.comparing(City::getArea))
                .orElseThrow(() -> {
                    logger.error("No cities found to calculate the largest city");
                    return new RuntimeException("No cities found");
                });

        Coordinates coordinates = largestCity.getCoordinates();
        Float x = coordinates.getX();
        double y = coordinates.getY();

        if (x == null || x <= -586) {
            logger.error("Invalid coordinates for largest city: x={}, y={}", x, y);
            throw new IllegalArgumentException("Invalid coordinates: x must be greater than -586 and cannot be null");
        }

        double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        logger.info("Distance to the largest city (ID: {}): {}", largestCity.getId(), distance);
        return distance;
    }

    public List<City> getCitiesByUserId(Long userId) {
        logger.info("Fetching cities by user ID: {}", userId);
        return cityRepository.findByUserId(userId);
    }

    public Page<City> getPaginatedCities(Pageable pageable) {
        logger.info("Fetching paginated cities with pageable: {}", pageable);
        return cityRepository.findAll(pageable);
    }

    public List<CityHistoryDto> getCityHistory() {
        logger.info("Fetching city history");
        List<City> cities = cityRepository.findAll();

        List<CityHistoryDto> historyList = new ArrayList<>();
        for (City city : cities) {
            CityHistoryDto history = new CityHistoryDto();

            history.setCityId(city.getId());
            history.setCityName(city.getName());

            history.setCreatedBy(city.getUser() != null ? city.getUser().getUsername() : "N/A");
            history.setCreatedDate(city.getCreationDate() != null ? city.getCreationDate().toString() : "N/A");

            history.setUpdatedBy(city.getUpdatedBy() != null ? city.getUpdatedBy().getUsername() : "N/A");
            history.setUpdatedDate(city.getUpdatedDate() != null ? city.getUpdatedDate().toString() : "N/A");

            logger.info("City history entry: {}", history);
            historyList.add(history);
        }

        return historyList;
    }

    public void importCities(MultipartFile file, OAuthUser user) throws IOException {
        logger.info("Importing cities from file: {}", file.getOriginalFilename());
        ImportHistory history = new ImportHistory();
        history.setUser(user);
        history.setTimestamp(LocalDateTime.now());
        history.setStatus("IN_PROGRESS");
        importHistoryRepository.save(history);

        int importedCount = 0;
        try {
            List<City> cities = parseFile(file);

            for (City city : cities) {
                logger.info("Validating and saving city: {}", city);
                validateCity(city);
                city.setUser(user);

                if (city.getCoordinates().getId() == null) {
                    logger.info("Saving new coordinates: {}", city.getCoordinates());
                    Coordinates savedCoordinates = coordinatesRepository.save(city.getCoordinates());
                    city.setCoordinates(savedCoordinates);
                } else {
                    logger.info("Using existing coordinates with ID: {}", city.getCoordinates().getId());
                }

                if (city.getGovernor().getId() == null) {
                    logger.info("Saving new governor: {}", city.getGovernor());
                    Human savedGovernor = humanRepository.save(city.getGovernor());
                    city.setGovernor(savedGovernor);
                } else {
                    logger.info("Using existing governor with ID: {}", city.getGovernor().getId());
                }

                cityRepository.save(city);
                logger.info("City imported: {}", city);
                importedCount++;
            }
            history.setStatus("SUCCESS");
            history.setObjectsImported(importedCount);
        } catch (Exception e){
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.length() > 255) {
                errorMessage = errorMessage.substring(0, 255);
            }
            history.setStatus("FAILED");
            history.setErrorMessage(errorMessage);
            logger.error("Error during import: {}", e.getMessage(), e);
        } finally {
            importHistoryRepository.save(history);
        }
    }


    private List<City> parseFile(MultipartFile file) throws IOException {
        logger.info("Parsing file: {}", file.getOriginalFilename());
        List<City> cities = yamlMapper.readValue(file.getInputStream(),
                yamlMapper.getTypeFactory().constructCollectionType(List.class, City.class));
        logger.info("Parsed {} cities from file", cities.size());
        return cities;
    }

    private void validateCity(City city) {
        logger.info("Validating city: {}", city);
        if (city.getName() == null || city.getName().isEmpty()) {
            logger.error("City name cannot be empty: {}", city);
            throw new InvalidDataException("City name cannot be empty");
        }
        if (city.getArea() <= 0) {
            logger.error("City area must be greater than 0: {}", city);
            throw new InvalidDataException("City area must be greater than 0");
        }
        if (city.getPopulation() == null || city.getPopulation() <= 0) {
            logger.error("City population must be greater than 0: {}", city);
            throw new InvalidDataException("City population must be greater than 0");
        }
        if (city.getClimate() == null) {
            logger.error("City climate cannot be null: {}", city);
            throw new InvalidDataException("City climate cannot be null");
        }
        if (city.getGovernor() == null) {
            logger.error("Governor cannot be null: {}", city);
            throw new InvalidDataException("Governor cannot be null");
        }
        if (city.getCoordinates() == null) {
            logger.error("Coordinates cannot be null: {}", city);
            throw new InvalidDataException("Coordinates cannot be null");
        }
    }



}

