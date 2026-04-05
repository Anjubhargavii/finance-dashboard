package com.finance.dashboard.service;

import com.finance.dashboard.dto.FinancialRecordDTO;
import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;

    public FinancialRecord createRecord(FinancialRecordDTO dto, String username) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(dto.getDate());
        record.setNotes(dto.getNotes());
        record.setCreatedBy(username);
        return recordRepository.save(record);
    }

    public List<FinancialRecord> getAllRecords() {
        return recordRepository.findByDeletedFalse();
    }

    public List<FinancialRecord> getRecordsByType(RecordType type) {
        return recordRepository.findByTypeAndDeletedFalse(type);
    }

    public List<FinancialRecord> getRecordsByCategory(String category) {
        return recordRepository.findByCategoryAndDeletedFalse(category);
    }

    public List<FinancialRecord> getRecordsByDateRange(LocalDate start, LocalDate end) {
        return recordRepository.findByDateBetweenAndDeletedFalse(start, end);
    }

    public FinancialRecord updateRecord(Long id, FinancialRecordDTO dto) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(dto.getDate());
        record.setNotes(dto.getNotes());
        return recordRepository.save(record);
    }

    public void softDeleteRecord(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        record.setDeleted(true);
        recordRepository.save(record);
    }

    public BigDecimal getTotalIncome() {
        BigDecimal result = recordRepository.sumByType(RecordType.INCOME);
        return result != null ? result : BigDecimal.ZERO;
    }

    public BigDecimal getTotalExpenses() {
        BigDecimal result = recordRepository.sumByType(RecordType.EXPENSE);
        return result != null ? result : BigDecimal.ZERO;
    }

    public BigDecimal getNetBalance() {
        return getTotalIncome().subtract(getTotalExpenses());
    }

    public List<Object[]> getCategoryTotals() {
        return recordRepository.sumByCategory();
    }
}