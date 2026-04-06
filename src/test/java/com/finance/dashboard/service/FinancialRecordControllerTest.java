package com.finance.dashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.dashboard.dto.FinancialRecordDTO;
import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.security.JwtFilter;
import com.finance.dashboard.security.JwtUtil;
import com.finance.dashboard.security.RateLimitFilter;
import com.finance.dashboard.service.FinancialRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = FinancialRecordController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { JwtFilter.class, RateLimitFilter.class }
    )
)
class FinancialRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FinancialRecordService recordService;

    @MockBean
    private JwtUtil jwtUtil;

    private FinancialRecordDTO validDto() {
        FinancialRecordDTO dto = new FinancialRecordDTO();
        dto.setAmount(new BigDecimal("500.00"));
        dto.setType(RecordType.INCOME);
        dto.setCategory("Salary");
        dto.setDate(LocalDate.of(2026, 4, 1));
        dto.setNotes("Test");
        return dto;
    }

    // --- POST /api/records ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRecord_validInput_returns200() throws Exception {
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);

        when(recordService.createRecord(any(), any())).thenReturn(record);

        mockMvc.perform(post("/api/records")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Record created successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRecord_missingAmount_returns400() throws Exception {
        FinancialRecordDTO dto = validDto();
        dto.setAmount(null);

        mockMvc.perform(post("/api/records")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.amount").value("Amount is required"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRecord_negativeAmount_returns400() throws Exception {
        FinancialRecordDTO dto = validDto();
        dto.setAmount(new BigDecimal("-100"));

        mockMvc.perform(post("/api/records")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.amount").value("Amount must be positive"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void createRecord_viewerRole_isBlocked() throws Exception {
        mockMvc.perform(post("/api/records")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto())))
                .andExpect(status().is4xxClientError());
    }

    // --- GET /api/records ---

    @Test
    @WithMockUser(roles = "VIEWER")
    void getAllRecords_viewerRole_returns200() throws Exception {
        when(recordService.getAllRecords(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/records"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRecords_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/records"))
                .andExpect(status().isUnauthorized());
    }

    // --- PUT /api/records/{id} ---

    @Test
    @WithMockUser(roles = "ANALYST")
    void updateRecord_analystRole_returns200() throws Exception {
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        when(recordService.updateRecord(any(), any())).thenReturn(record);

        mockMvc.perform(put("/api/records/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto())))
                .andExpect(status().isOk());
    }

    // --- DELETE /api/records/{id} ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRecord_adminRole_returns200() throws Exception {
        mockMvc.perform(delete("/api/records/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Record deleted successfully"));
    }
}