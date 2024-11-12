package git.dimitrikvirik.sap.model.param;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class CityCsvParam {

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "area")
    private double area;

    @CsvBindByName(column = "population")
    private long population;
}
