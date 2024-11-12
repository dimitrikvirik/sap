package git.dimitrikvirik.sap.model.entity;

import git.dimitrikvirik.sap.model.param.CityCsvParam;
import git.dimitrikvirik.sap.model.param.CityParam;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "CITY")
@Data
public class City {

    private static final double SQ_MILES_TO_SQ_KILOMETERS = 2.58998811;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "POPULATION")
    private Long population;

    @Column(name = "AREA")
    private Double area; // in square miles

    @Column(name = "DENSITY")
    private Double density; // people per sq km

    @PreUpdate
    @PrePersist
    public void calculateDensity() {
        if(population != null && area != null){
            // Convert area from square miles to square kilometers
            double areaInKm = area * SQ_MILES_TO_SQ_KILOMETERS;
            this.density = population / areaInKm;
        }
    }

    public static City fromParam(CityParam param){
        City city = new City();
        city.setName(param.getName());
        city.setPopulation(param.getPopulation());
        city.setArea(param.getArea());
        return city;
    }

    public static City fromParam(CityCsvParam param){
        City city = new City();
        city.setName(param.getName());
        city.setPopulation(param.getPopulation());
        city.setArea(param.getArea());
        return city;
    }

}
