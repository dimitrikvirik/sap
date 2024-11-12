package git.dimitrikvirik.sap;

import com.fasterxml.jackson.databind.ObjectMapper;
import git.dimitrikvirik.sap.model.dto.CityCsvDTO;
import git.dimitrikvirik.sap.model.dto.ErrorDTO;
import git.dimitrikvirik.sap.model.param.CityCsvParam;
import git.dimitrikvirik.sap.repository.CityRepository;
import git.dimitrikvirik.sap.service.CityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CityCsvControllerIntegrationTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CityService cityService;

    @BeforeEach
    void setup() {
        cityRepository.deleteAll();
    }

    @Test
    void testImportCitiesFromCsv() throws Exception {
        String csvContent = "name;area;population\n" +
                "New York;468.9;8398748\n" +
                "Los Angeles;468.7;3990456\n" +
                "Chicago;227.3;2705994\n" +
                "Houston;599.6;2320268\n" +
                "Phoenix;517.6;1680992\n" +
                "Philadelphia;369.4;1584064";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cities.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes()
        );

        mockMvc.perform(multipart("/city/csv/import").file(file))
                .andExpect(status().isCreated());

        // Verify that cities were imported
        List<CityCsvDTO> importedCities = cityService.getAllCities(null, null)
                .stream()
                .map(CityCsvDTO::fromEntity)
                .toList();

        assertThat(importedCities).hasSize(6);
        assertThat(importedCities).extracting("name")
                .containsExactlyInAnyOrder("New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia");
    }

    @Test
    void testExportCitiesToCsv() throws Exception {
        // First, import some cities
        String csvContent = "name;area;population\n" +
                "Tokyo;2194.0;13929286\n" +
                "Delhi;1484.0;16787941\n" +
                "Shanghai;6340.5;24256800";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cities.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes()
        );

        mockMvc.perform(multipart("/city/csv/import").file(file))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/city/csv/export")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=cities.csv"))
                .andReturn();

        String exportedCsvContent = result.getResponse().getContentAsString();
        List<String> lines = exportedCsvContent.lines().toList();

        assertThat(lines).hasSize(4); // Header + 3 cities
        assertThat(lines.get(0)).isEqualTo("\"id\",\"name\",\"area\",\"density\",\"population\"");
        assertThat(lines.get(1)).contains("\"Delhi\"");
        assertThat(lines.get(2)).contains("\"Shanghai\"");
        assertThat(lines.get(3)).contains("\"Tokyo\"");
    }

    @Test
    void testExportCitiesWithNameFilter() throws Exception {
        // First, import some cities
        String csvContent = "name;area;population\n" +
                "New York;468.9;8398748\n" +
                "Los Angeles;468.7;3990456\n" +
                "Chicago;227.3;2705994";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cities.csv",
                MediaType.TEXT_PLAIN_VALUE,
                csvContent.getBytes()
        );

        mockMvc.perform(multipart("/city/csv/import").file(file))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/city/csv/export")
                        .param("name", "Los"))
                .andExpect(status().isOk())
                .andReturn();

        String exportedCsvContent = result.getResponse().getContentAsString();
        List<String> lines = exportedCsvContent.lines().toList();

        assertThat(lines).hasSize(2); // Header + 1 city
        assertThat(lines.get(0)).isEqualTo("\"id\",\"name\",\"area\",\"density\",\"population\"");
        assertThat(lines.get(1)).contains("\"Los Angeles\"");
    }

    @Test
    void testImportCsvWithDataTypeMismatch() throws Exception {
        String invalidCsvContent = "name;area;population\n" +
                "New York;468.9;eight million"; // Invalid population data

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid_cities.csv",
                MediaType.TEXT_PLAIN_VALUE,
                invalidCsvContent.getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/city/csv/import").file(file))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ErrorDTO errorDTO = objectMapper.readValue(responseContent, ErrorDTO.class);

        assertThat(errorDTO.getTimestamp()).isNotNull();
        assertThat(errorDTO.getMessage()).contains("Failed to read CSV data");
        assertThat(errorDTO.getMessage()).contains("Error parsing CSV line: 2. [New York,468.9,eight million]");
    }
}