package eoeqs.service;

import eoeqs.exceptions.ResourceNotFoundException;
import eoeqs.model.City;
import eoeqs.repository.CityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    @Autowired
    private CityRepository cityRepository;

    @Transactional
    public City createCity(City city) {
        return cityRepository.save(city);
    }

    public Optional<City> getCityById(long id) {
        return cityRepository.findById(id);
    }

    @Transactional
    public City updateCity(long id, City updatedCity) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found"));
        city.setName(updatedCity.getName());
        city.setCoordinates(updatedCity.getCoordinates());
        city.setArea(updatedCity.getArea());
        city.setPopulation(updatedCity.getPopulation());
        city.setCapital(updatedCity.getCapital());
        city.setMetersAboveSeaLevel(updatedCity.getMetersAboveSeaLevel());
        city.setCarCode(updatedCity.getCarCode());
        city.setAgglomeration(updatedCity.getAgglomeration());
        city.setClimate(updatedCity.getClimate());
        city.setGovernor(updatedCity.getGovernor());
        return cityRepository.save(city);
    }

    @Transactional
    public void deleteCity(long id) {
        cityRepository.deleteById(id);
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }
}
