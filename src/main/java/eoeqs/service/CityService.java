package eoeqs.service;

import eoeqs.model.City;
import eoeqs.repository.CityRepository;
import eoeqs.repository.HumanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
                    existingCity.setName(city.getName());
                    existingCity.setCoordinates(city.getCoordinates());
                    existingCity.setArea(city.getArea());
                    existingCity.setPopulation(city.getPopulation());
                    existingCity.setEstablishmentDate(city.getEstablishmentDate());
                    existingCity.setCapital(city.getCapital());
                    existingCity.setMetersAboveSeaLevel(city.getMetersAboveSeaLevel());
                    existingCity.setCarCode(city.getCarCode());
                    existingCity.setAgglomeration(city.getAgglomeration());
                    existingCity.setClimate(city.getClimate());
                    existingCity.setGovernor(city.getGovernor());
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
}
