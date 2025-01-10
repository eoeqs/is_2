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
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CityService {
    private static final Logger logger = LoggerFactory.getLogger(CityService.class);

    private final CityRepository cityRepository;
    private final HumanRepository humanRepository;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final CoordinatesRepository coordinatesRepository;
    private final ImportHistoryRepository importHistoryRepository;
    private final ImportHistoryService importHistoryService;


    public CityService(CityRepository cityRepository, HumanRepository humanRepository, CoordinatesRepository coordinatesRepository, ImportHistoryRepository importHistoryRepository, ImportHistoryService importHistoryService) {
        this.cityRepository = cityRepository;
        this.humanRepository = humanRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.importHistoryRepository = importHistoryRepository;
        this.importHistoryService = importHistoryService;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public City createCity(City city) {
        logger.trace("Creating city with data: {}", city);
        if (city.getGovernor() != null && city.getGovernor().getId() == null) {
            logger.trace("Governor data is new, saving separately: {}", city.getGovernor());
            city.setGovernor(humanRepository.saveAndFlush(city.getGovernor()));
            logger.trace("Saved Governor with ID: {}", city.getGovernor().getId());
        }

        if (city.getCoordinates() != null && city.getCoordinates().getId() == null) {
            logger.trace("Coordinates are new, saving separately: {}", city.getCoordinates());
            city.setCoordinates(coordinatesRepository.saveAndFlush(city.getCoordinates()));
            logger.trace("Saved Coordinates with ID: {}", city.getCoordinates().getId());
        }

        if (city.getGovernor() == null || city.getCoordinates() == null) {
            logger.error("City must have a valid governor and coordinates. Governor: {}, Coordinates: {}", city.getGovernor(), city.getCoordinates());
            throw new InvalidDataException("City must have a valid governor and coordinates.");
        }
        validateCityUniqueness(city);

        City savedCity = cityRepository.save(city);

        logger.debug("City created with ID: {}", savedCity.getId());
        return savedCity;
    }

    private void validateCityUniqueness(City city) {
        if (city.getCapital() && city.getAgglomeration() > 0) {
            throw new InvalidDataException("A city cannot be both a capital and have agglomeration status.");
        }
        if (city.getMetersAboveSeaLevel() > 3000 && city.getAgglomeration() > 0) {
            logger.debug("City cannot be an agglomeration if its height above sea level exceeds 3000 meters: {}", city);
            throw new InvalidDataException("A city cannot be an agglomeration if its height above sea level exceeds 3000 meters.");
        }

        List<City> citiesWithSameCoordinates = cityRepository.findByCoordinates(city.getCoordinates());
        for (City existingCity : citiesWithSameCoordinates) {
            if (!existingCity.getPopulation().equals(city.getPopulation())) {
                throw new InvalidDataException("Cities with the same coordinates must have the same population.");
            }
        }

        Optional<City> existingCityWithSameNameAndPopulation = cityRepository.findByNameAndPopulation(city.getName(), city.getPopulation());
        if (existingCityWithSameNameAndPopulation.isPresent()) {
            throw new InvalidDataException("A city with the same name and population already exists.");
        }
    }


    public Optional<City> getCityById(Long id) {
        logger.info("Fetching city by ID: {}", id);
        return cityRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public City updateCity(Long id, City city) {
        logger.info("Updating city with ID: {} and data: {}", id, city);
        validateCityUniqueness(city);

        int maxRetries = 3;
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                return cityRepository.findById(id)
                        .map(existingCity -> {
                            logger.info("Found existing city: {}", existingCity);

                            Long oldVersion = existingCity.getVersion();

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

                            if (existingCity.getVersion() == null || !existingCity.getVersion().equals(oldVersion)) {
                                logger.warn("Version mismatch detected for city ID: {}. Retrying...", id);
                                throw new OptimisticLockException("City version mismatch. Retrying operation.");
                            }

                            logger.info("City updated: {}", existingCity);
                            return cityRepository.save(existingCity);
                        })
                        .orElseThrow(() -> {
                            logger.error("City with ID {} not found for update", id);
                            return new RuntimeException("City not found");
                        });

            } catch (OptimisticLockException e) {
                logger.warn("Optimistic lock exception encountered on attempt {}/{}. Retrying...", attempt + 1, maxRetries);
                attempt++;
                if (attempt >= maxRetries) {
                    logger.error("Max retry attempts reached. Operation failed.");
                    throw e;
                }
            } catch (Exception e) {
                logger.error("Error during city update: {}", e.getMessage(), e);
                throw new RuntimeException("Error updating city", e);
            }
        }
        return null;
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

    @Transactional(rollbackFor = Exception.class)
    public void importCities(MultipartFile file, OAuthUser user) throws IOException {
        logger.trace("Importing cities from file: {}", file.getOriginalFilename());


        int importedCount = 0;
        boolean isErrorLogged = false;
        try {
            List<City> cities = parseFile(file);
            List<City> failedCities = new ArrayList<>();
            List<City> existingCities = cityRepository.findAll();
            List<City> citiesToSave = new ArrayList<>();
            Map<Long, Human> existingHumans = humanRepository.findAll().stream()
                    .collect(Collectors.toMap(Human::getId, human -> human));
            Map<Long, Coordinates> existingCoordinates = coordinatesRepository.findAll().stream()
                    .collect(Collectors.toMap(Coordinates::getId, coordinates -> coordinates));
            for (City city : cities) {
                logger.trace("Validating and saving city: {}", city);

                city.setUser(user);
                validateCity(city);
                boolean isSimilar = isSimilarToExisting(city, existingCities);
                if (isSimilar) {
                    logger.debug("City is too similar to an existing one, skipping: {}", city.getName());
                    failedCities.add(city);
                    continue;
                }
                if (city.getCoordinates().getId() == null) {
                    logger.trace("Saving new coordinates: {}", city.getCoordinates());
                    Coordinates savedCoordinates = coordinatesRepository.saveAndFlush(city.getCoordinates());
                    city.setCoordinates(savedCoordinates);
                } else {
                    logger.trace("Using existing coordinates with ID: {}", city.getCoordinates().getId());
                    city.setCoordinates(existingCoordinates.get(city.getCoordinates().getId()));
                }
                if (city.getGovernor().getId() == null) {
                    logger.trace("Saving new governor: {}", city.getGovernor());
                    Human savedGovernor = humanRepository.saveAndFlush(city.getGovernor());
                    city.setGovernor(savedGovernor);
                } else {
                    logger.trace("Using existing governor with ID: {}", city.getGovernor().getId());
                    city.setGovernor(existingHumans.get(city.getGovernor().getId()));

                }
                validateCityUniqueness(city);

                citiesToSave.add(city);

                importedCount++;
            }

            if (!failedCities.isEmpty()) {
                logger.error("Some cities failed to import due to similarity: {}", failedCities);
                importHistoryService.saveImportHistory(user, "FAILED", null, "Some cities were too similar to existing data and were skipped.");
                isErrorLogged = true;
                throw new InvalidDataException("Some cities were too similar to existing data and were skipped.");
            }
            cityRepository.saveAll(citiesToSave);

            importHistoryService.saveImportHistory(user, "SUCCESS", importedCount, null);

            logger.info("Imported {} cities successfully", importedCount);

        } catch (Exception e) {
            String errorMessage = e.getMessage();

            if (errorMessage != null && errorMessage.length() > 255) {
                errorMessage = errorMessage.substring(0, 255);
            }
            if (!isErrorLogged) {
                importHistoryService.saveImportHistory(user, "FAILED", null, errorMessage);
            }            logger.error("Error during import: {}", e.getMessage(), e);
            throw new InvalidDataException("Error during import.");
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
        logger.trace("Validating city: {}", city);
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

    private boolean isSimilarToExisting(City city, List<City> existingCities) {
        for (City existingCity : existingCities) {
            if (isContentSimilar(existingCity, city)) {
                return true;
            }
        }
        return false;
    }


    public boolean isContentSimilar(City city1, City city2) {
        int totalFields = 9;
        int matchingFields = 0;

        if (city1.getArea() == city2.getArea()) matchingFields++;
        if (Objects.equals(city1.getPopulation(), city2.getPopulation())) matchingFields++;
        if (city1.getCapital() == city2.getCapital()) matchingFields++;
        if (city1.getMetersAboveSeaLevel() == city2.getMetersAboveSeaLevel()) matchingFields++;
        if (Objects.equals(city1.getCarCode(), city2.getCarCode())) matchingFields++;
        if (city1.getAgglomeration() == city2.getAgglomeration()) matchingFields++;
        if (city1.getClimate().equals(city2.getClimate())) matchingFields++;
        if (Objects.equals(city1.getCoordinates().getX(), city2.getCoordinates().getX())) matchingFields++;
        if (city1.getCoordinates().getY() == city2.getCoordinates().getY()) matchingFields++;

        double similarityPercentage = (double) matchingFields / totalFields * 100;
        return similarityPercentage >= 60;
    }
}

