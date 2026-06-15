package com.danielsanoli.fusionia.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FusionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.provider").value("stub"));
    }

    @Test
    void shouldCreateFindListAndReturnStubImage() throws Exception {
        String payload = """
                {
                  "pokemons": ["charizard", "gengar"],
                  "style": "dark fantasy sprite",
                  "dominantColor": "purple",
                  "seed": 123
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/fusions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.pokemons[0]").value("charizard"))
                .andExpect(jsonPath("$.pokemons[1]").value("gengar"))
                .andExpect(jsonPath("$.status").value("READY"))
                .andExpect(jsonPath("$.provider").value("stub"))
                .andExpect(jsonPath("$.prompt", containsString("Charizard and Gengar")))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String id = responseBody.replaceAll(".*\\\"id\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(get("/api/v1/fusions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(get("/api/v1/fusions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/v1/fusions/{id}/image", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("stub"))
                .andExpect(jsonPath("$.message", containsString("stub provider")));
    }

    @Test
    void shouldReturnBadRequestForInvalidPokemonList() throws Exception {
        String payload = """
                {
                  "pokemons": ["pikachu"]
                }
                """;

        mockMvc.perform(post("/api/v1/fusions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)));
    }
}

