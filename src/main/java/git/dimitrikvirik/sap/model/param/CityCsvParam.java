package git.dimitrikvirik.sap.model.param;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityCsvParam {

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "area")
    private double area;

    @CsvBindByName(column = "population")
    private long population;
}
