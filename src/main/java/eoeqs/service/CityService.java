package eoeqs.service;

import eoeqs.dao.CityDAO;
import eoeqs.dao.UserDAO;
import eoeqs.model.City;
import eoeqs.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityDAO cityDAO;
    private final UserDAO userDAO;

    public City getCityById(Long id) {
        Optional<City> city = cityDAO.findById(id);
        if (city.isEmpty()) {
            throw new RuntimeException("City not found");
        }
        return city.get();
    }

    public List<City> getAllCitiesByUser(String username) {
        User user = userDAO.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        return cityDAO.findAll().stream()
                .filter(city -> city.getUser().equals(user))
                .collect(Collectors.toList());
    }

    public City createCity(City city, String username) {
        User user = userDAO.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        city.setUser(user);
        cityDAO.save(city);
        return city;
    }

    public String updateCity(Long id, City updatedCity, String username) {
        City city = cityDAO.findById(id).orElse(null);
        if (city == null) {
            return "City not found";
        }

        if (!city.getUser().getUsername().equals(username)) {
            return "You are not allowed to update this city";
        }

        city.setName(updatedCity.getName());
        city.setCoordinates(updatedCity.getCoordinates());
        city.setArea(updatedCity.getArea());
        city.setPopulation(updatedCity.getPopulation());
        city.setEstablishmentDate(updatedCity.getEstablishmentDate());
        city.setCapital(updatedCity.getCapital());
        city.setMetersAboveSeaLevel(updatedCity.getMetersAboveSeaLevel());
        city.setCarCode(updatedCity.getCarCode());
        city.setAgglomeration(updatedCity.getAgglomeration());
        city.setClimate(updatedCity.getClimate());
        city.setGovernor(updatedCity.getGovernor());

        cityDAO.save(city);
        return "City updated successfully";
    }

    public String deleteCity(Long id, String username) {
        City city = cityDAO.findById(id).orElse(null);
        if (city == null) {
            return "City not found";
        }

        if (!city.getUser().getUsername().equals(username)) {
            return "You are not allowed to delete this city";
        }

        cityDAO.delete(city);
        return "City deleted successfully";
    }
}