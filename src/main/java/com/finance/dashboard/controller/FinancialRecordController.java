package com.finance.dashboard.controller;

import com.finance.dashboard.dto.FinancialRecordDTO;
import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    @PostMapping
    public ResponseEntity<?> createRecord(@Valid @RequestBody FinancialRecordDTO dto,
                                          Authentication auth) {
        try {
            FinancialRecord record = recordService.createRecord(dto, auth.getName());
            return ResponseEntity.ok(Map.of(
                "message", "Record created successfully",
                "recordId", record.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<FinancialRecord>> getAllRecords(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (type != null) return ResponseEntity.ok(recordService.getRecordsByType(type));
        if (category != null) return ResponseEntity.ok(recordService.getRecordsByCategory(category));
        if (startDate != null && endDate != null) return ResponseEntity.ok(recordService.getRecordsByDateRange(startDate, endDate));
        return ResponseEntity.ok(recordService.getAllRecords());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecord(@PathVariable Long id,
                                          @Valid @RequestBody FinancialRecordDTO dto) {
        try {
            FinancialRecord record = recordService.updateRecord(id, dto);
            return ResponseEntity.ok(Map.of("message", "Record updated", "recordId", record.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long id) {
        try {
            recordService.softDeleteRecord(id);
            return ResponseEntity.ok(Map.of("message", "Record deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}