package git.dimitrikvirik.sap.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Valid
public class CityParam {

    @JsonProperty("name")
    @NotBlank
    private String name;

    @JsonProperty("population")
    @Min(0)
    private Long population;

    @JsonProperty("area")
    @Min(0)
    private Double area;

}
