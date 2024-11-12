package git.dimitrikvirik.sap;


import com.fasterxml.jackson.databind.ObjectMapper;
import git.dimitrikvirik.sap.model.param.CityParam;
import git.dimitrikvirik.sap.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CityControllerIntegrationTest extends AbstractTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CityRepository cityRepository;


    @BeforeEach
    void setup() throws Exception {
        cityRepository.deleteAll();
        // Import some initial data
        List<CityParam> initialCities = Arrays.asList(
                new CityParam("New York", 8_400_000L, 783.8),
                new CityParam("Los Angeles", 3_900_000L, 1_302.0),
                new CityParam("Chicago", 2_700_000L, 606.1)
        );

        mockMvc.perform(post("/city")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initialCities)))
                .andExpect(status().isCreated());

    }


    @Test
    void testGetAllCities() throws Exception {
        mockMvc.perform(get("/city"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("New York")))
                .andExpect(jsonPath("$[1].name", is("Los Angeles")))
                .andExpect(jsonPath("$[2].name", is("Chicago")));
    }

    @Test
    void testGetAllCitiesWithNameFilter() throws Exception {
        mockMvc.perform(get("/city").param("name", "Los"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Los Angeles")));
    }

    @Test
    void testGetAllCitiesWithSorting() throws Exception {
        mockMvc.perform(get("/city").param("sort", "population,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("New York")))
                .andExpect(jsonPath("$[1].name", is("Los Angeles")))
                .andExpect(jsonPath("$[2].name", is("Chicago")));
    }

    @Test
    void testImportCities() throws Exception {
        List<CityParam> newCities = Arrays.asList(
                new CityParam("San Francisco", 880_000L, 121.4),
                new CityParam("Houston", 2_300_000L, 1_651.0)
        );

        mockMvc.perform(post("/city")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCities)))
                .andExpect(status().isCreated());

        // Verify the new cities were added
        mockMvc.perform(get("/city"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[*].name", hasItems("San Francisco", "Houston")));
    }

    @Test
    void testImportCitiesWithInvalidData() throws Exception {
        List<CityParam> invalidCities = List.of(
                new CityParam("", -1L, -1.0)
        );

        mockMvc.perform(post("/city")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCities)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value(containsString("Validation failed:")))
                .andExpect(jsonPath("$.message").value(containsString("cities[0].name=must not be blank")))
                .andExpect(jsonPath("$.message").value(containsString("cities[0].population=must be greater than or equal to 0")))
                .andExpect(jsonPath("$.message").value(containsString("cities[0].area=must be greater than or equal to 0")));
    }
}