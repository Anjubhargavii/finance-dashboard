package com.finance.dashboard.controller;

import com.finance.dashboard.dto.FinancialRecordDTO;
import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    @PostMapping
    public ResponseEntity<?> createRecord(@Valid @RequestBody FinancialRecordDTO dto,
                                          Authentication auth) {
        FinancialRecord record = recordService.createRecord(dto, auth.getName());
        return ResponseEntity.ok(Map.of(
                "message", "Record created successfully",
                "recordId", record.getId()
        ));
    }

    // Supports: pagination, sorting, filtering by type/category/date, and keyword search
    // Examples:
    //   GET /api/records?page=0&size=10&sort=date,desc
    //   GET /api/records?type=INCOME&page=0&size=5
    //   GET /api/records?category=Salary&page=0&size=10
    //   GET /api/records?startDate=2026-01-01&endDate=2026-04-01
    //   GET /api/records?search=rent&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<FinancialRecord>> getAllRecords(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date,desc") String sort) {

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(recordService.searchRecords(search, pageable));
        }
        if (type != null) {
            return ResponseEntity.ok(recordService.getRecordsByType(type, pageable));
        }
        if (category != null) {
            return ResponseEntity.ok(recordService.getRecordsByCategory(category, pageable));
        }
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(recordService.getRecordsByDateRange(startDate, endDate, pageable));
        }
        return ResponseEntity.ok(recordService.getAllRecords(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecord(@PathVariable Long id,
                                          @Valid @RequestBody FinancialRecordDTO dto) {
        FinancialRecord record = recordService.updateRecord(id, dto);
        return ResponseEntity.ok(Map.of("message", "Record updated", "recordId", record.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long id) {
        recordService.softDeleteRecord(id);
        return ResponseEntity.ok(Map.of("message", "Record deleted successfully"));
    }
}