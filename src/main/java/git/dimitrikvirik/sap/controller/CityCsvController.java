package git.dimitrikvirik.sap.controller;

import git.dimitrikvirik.sap.model.dto.CityCsvDTO;
import git.dimitrikvirik.sap.model.param.CityCsvParam;
import git.dimitrikvirik.sap.service.CityService;
import git.dimitrikvirik.sap.utils.CsvUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/city/csv")
@RequiredArgsConstructor
@Tag(name = "City Management", description = "APIs for managing city data through CSV")
@Validated
public class CityCsvController {

    private final CityService cityService;

    @Operation(summary = "Import cities from CSV", description = "Import a list of cities into the system from a CSV file")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importCitiesFromCsv(
            @RequestPart("file")
            @Parameter(description = "Upload CSV file", required = true)
            MultipartFile file) {
        cityService.importCsvCities(CsvUtils.read(CityCsvParam.class, file));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Export cities to CSV", description = "Export a list of cities to a CSV file")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCitiesToCsv(
            @Parameter(description = "Filter cities by name (optional)")
            @RequestParam(required = false) String name,
            @Parameter(
                    description = "Sorting criteria in the format: property,(asc|desc). Multiple sort criteria are supported.",
                    example = "name,asc",
                    schema = @Schema(type = "string", allowableValues = {"name,asc", "name,desc", "population,asc", "population,desc", "area,asc", "area,desc"})
            ) Sort sort
    ) {
        List<CityCsvDTO> cities = cityService.getAllCities(sort, name).stream().map(CityCsvDTO::fromEntity).toList();
        byte[] csvContent = CsvUtils.write(cities, CityCsvDTO.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cities.csv");
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }


}