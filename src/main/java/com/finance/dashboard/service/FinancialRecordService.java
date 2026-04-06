package com.finance.dashboard.service;

import com.finance.dashboard.dto.FinancialRecordDTO;
import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;

    // --- Create ---
    public FinancialRecord createRecord(FinancialRecordDTO dto, String createdBy) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(dto.getDate());
        record.setNotes(dto.getNotes());
        record.setCreatedBy(createdBy);
        record.setCreatedAt(LocalDateTime.now());
        record.setDeleted(false);
        return recordRepository.save(record);
    }

    // --- Read (paginated) ---
    public Page<FinancialRecord> getAllRecords(Pageable pageable) {
        return recordRepository.findByDeletedFalse(pageable);
    }

    public Page<FinancialRecord> getRecordsByType(RecordType type, Pageable pageable) {
        return recordRepository.findByTypeAndDeletedFalse(type, pageable);
    }

    public Page<FinancialRecord> getRecordsByCategory(String category, Pageable pageable) {
        return recordRepository.findByCategoryContainingIgnoreCaseAndDeletedFalse(category, pageable);
    }

    public Page<FinancialRecord> getRecordsByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        return recordRepository.findByDateBetweenAndDeletedFalse(start, end, pageable);
    }

    // --- Search (keyword across category + notes) ---
    public Page<FinancialRecord> searchRecords(String keyword, Pageable pageable) {
        return recordRepository.searchByKeyword(keyword, pageable);
    }

    // --- Update ---
    public FinancialRecord updateRecord(Long id, FinancialRecordDTO dto) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));
        if (record.isDeleted()) {
            throw new RuntimeException("Cannot update a deleted record");
        }
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(dto.getDate());
        record.setNotes(dto.getNotes());
        return recordRepository.save(record);
    }

    // --- Soft Delete ---
    public void softDeleteRecord(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));
        record.setDeleted(true);
        recordRepository.save(record);
    }

    // --- Dashboard aggregations (non-paginated, used by DashboardController) ---
    public BigDecimal getTotalIncome() {
        BigDecimal total = recordRepository.sumByType(RecordType.INCOME);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalExpenses() {
        BigDecimal total = recordRepository.sumByType(RecordType.EXPENSE);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getNetBalance() {
        return getTotalIncome().subtract(getTotalExpenses());
    }

    public List<Object[]> getCategoryTotals() {
        return recordRepository.sumByCategory();
    }
}