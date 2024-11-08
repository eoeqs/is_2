package eoeqs.service;

import eoeqs.model.City;
import eoeqs.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public City createCity(City city) {
        return cityRepository.save(city);
    }

    public Optional<City> getCityById(Long id) {
        return cityRepository.findById(id);
    }

    public City updateCity(Long id, City city) {
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
                    return cityRepository.save(existingCity);
                }).orElseThrow(() -> new RuntimeException("City not found"));
    }

    public void deleteCity(Long id) {
        cityRepository.deleteById(id);
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }
}
