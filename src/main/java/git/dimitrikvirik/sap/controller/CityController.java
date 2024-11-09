package git.dimitrikvirik.sap.controller;

import git.dimitrikvirik.sap.model.entity.City;
import git.dimitrikvirik.sap.model.param.CityParam;
import git.dimitrikvirik.sap.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/city")
@RequiredArgsConstructor
@Tag(name = "City Management", description = "APIs for managing city data")
public class CityController {

    private final CityService cityService;

    @Operation(
            summary = "Get all cities",
            description = "Retrieve a list of cities with optional name filtering and sorting capabilities"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of cities",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = City.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid sort parameter provided",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<List<City>> getAllCities(
            @Parameter(description = "Filter cities by name (optional)")
            @RequestParam(required = false) String name,
            @Parameter(
                    description = "Sorting criteria in the format: property,(asc|desc). Multiple sort criteria are supported.",
                    example = "name,asc",
                    schema = @Schema(type = "string", allowableValues = {"name,asc", "name,desc", "population,asc", "population,desc", "area,asc", "area,desc"})
            ) Sort sort
    ) {
        return ResponseEntity.ok(cityService.getAllCities(sort, name));
    }

    @Operation(
            summary = "Import cities",
            description = "Import a list of cities into the system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Cities successfully imported",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid city data provided",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<Void> importCities(
            @Parameter(description = "List of cities to import", required = true)
            @RequestBody List<CityParam> cities
    ) {
        cityService.importCities(cities);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}