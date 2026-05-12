package com.company.assetmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchAssets_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/assets")
                        .param("environment", "production"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void searchAssets_withSuffixPattern_returnsNotFound() throws Exception {
        // Suffix pattern matching is now disabled, so requests with .json should return 404
        mockMvc.perform(get("/api/v1/assets.json")
                        .param("environment", "staging"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAsset_formEncoded_returnsCreated() throws Exception {
        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("hostname", "test-server-99")
                        .param("ipAddress", "10.99.99.99")
                        .param("assetType", "server")
                        .param("environment", "staging")
                        .param("osName", "Ubuntu")
                        .param("osVersion", "22.04"))
                .andExpect(status().isCreated());
    }

    @Test
    void getAsset_unknownId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/assets/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void importXml_validPayload_returnsOk() throws Exception {
        String xml = """
                <asset-list>
                  <asset>
                    <hostname>import-host-01</hostname>
                    <ip>10.20.30.40</ip>
                    <asset-type>server</asset-type>
                    <environment>production</environment>
                    <os-name>Ubuntu</os-name>
                    <os-version>20.04</os-version>
                  </asset>
                </asset-list>
                """;

        mockMvc.perform(post("/api/v1/assets/import/xml")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xml))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(1));
    }

    @Test
    void reportSummary_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/reports/summary")
                        .param("environment", "production"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAsset_unknownId_returnsNoContent() throws Exception {
        // Verifies the endpoint exists even when the ID is not found
        mockMvc.perform(delete("/api/v1/assets/888888"))
                .andExpect(status().isNoContent());
    }
}
