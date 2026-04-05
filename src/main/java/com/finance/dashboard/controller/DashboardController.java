package com.finance.dashboard.controller;

import com.finance.dashboard.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final FinancialRecordService recordService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalIncome", recordService.getTotalIncome());
        summary.put("totalExpenses", recordService.getTotalExpenses());
        summary.put("netBalance", recordService.getNetBalance());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/category-totals")
    public ResponseEntity<List<Object[]>> getCategoryTotals() {
        return ResponseEntity.ok(recordService.getCategoryTotals());
    }
}