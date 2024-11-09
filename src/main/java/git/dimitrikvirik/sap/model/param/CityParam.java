package git.dimitrikvirik.sap.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CityParam {

    @JsonProperty("name")
    private String name;

    @JsonProperty("population")
    private Long population;

    @JsonProperty("area")
    private Double area;

}
