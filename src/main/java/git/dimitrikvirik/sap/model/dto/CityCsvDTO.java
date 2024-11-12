package git.dimitrikvirik.sap.model.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import git.dimitrikvirik.sap.model.entity.City;
import lombok.Data;

@Data
public class CityCsvDTO {


    @CsvBindByName(column = "id")
    @CsvBindByPosition(position = 0)
    private long id;

    @CsvBindByName(column = "name")
    @CsvBindByPosition(position = 1)
    private String name;

    @CsvBindByName(column = "area")
    @CsvBindByPosition(position = 2)
    private double area;

    @CsvBindByName(column = "density")
    @CsvBindByPosition(position = 3)
    private double density;

    @CsvBindByName(column = "population")
    @CsvBindByPosition(position = 4)
    private long population;


    public static CityCsvDTO fromEntity(City city){
        CityCsvDTO cityCsvDTO = new CityCsvDTO();
        cityCsvDTO.setId(city.getId());
        cityCsvDTO.setName(city.getName());
        cityCsvDTO.setArea(city.getArea());
        cityCsvDTO.setDensity(city.getDensity());
        cityCsvDTO.setPopulation(city.getPopulation());
        return cityCsvDTO;
    }
}
