package git.dimitrikvirik.sap.service;

import git.dimitrikvirik.sap.model.entity.City;
import git.dimitrikvirik.sap.model.param.CityCsvParam;
import git.dimitrikvirik.sap.model.param.CityParam;
import git.dimitrikvirik.sap.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CityService {

    private final CityRepository cityRepository;

    @Transactional(readOnly = true)
    public List<City> getAllCities(Sort sort, String name) {
        if (name != null) {
            return cityRepository.findByNameContains(name, sort);
        }
        return cityRepository.findAll(sort);
    }

    public void importCities(List<CityParam> cities) {
        List<City> cityEntities = cities
                .stream()
                .map(City::fromParam)
                .toList();
        cityRepository.saveAll(cityEntities);
    }

    public void importCsvCities(List<CityCsvParam> cities) {
        List<City> cityEntities = cities
                .stream()
                .map(City::fromParam)
                .toList();
        cityRepository.saveAll(cityEntities);
    }
}
