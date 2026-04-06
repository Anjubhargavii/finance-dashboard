package com.finance.dashboard.service;

import com.finance.dashboard.dto.FinancialRecordDTO;
import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.repository.FinancialRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialRecordServiceTest {

    @Mock
    private FinancialRecordRepository recordRepository;

    @InjectMocks
    private FinancialRecordService recordService;

    private FinancialRecordDTO dto;
    private FinancialRecord record;

    @BeforeEach
    void setUp() {
        dto = new FinancialRecordDTO();
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setType(RecordType.INCOME);
        dto.setCategory("Salary");
        dto.setDate(LocalDate.of(2026, 4, 1));
        dto.setNotes("Monthly salary");

        record = new FinancialRecord();
        record.setId(1L);
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(dto.getDate());
        record.setNotes(dto.getNotes());
        record.setCreatedBy("admin");
        record.setDeleted(false);
    }

    @Test
    void createRecord_savesAndReturnsRecord() {
        when(recordRepository.save(any(FinancialRecord.class))).thenReturn(record);

        FinancialRecord result = recordService.createRecord(dto, "admin");

        assertNotNull(result);
        assertEquals("Salary", result.getCategory());
        assertEquals(RecordType.INCOME, result.getType());
        assertEquals(new BigDecimal("1000.00"), result.getAmount());
        verify(recordRepository, times(1)).save(any(FinancialRecord.class));
    }

    @Test
    void updateRecord_updatesAndReturnsRecord() {
        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(recordRepository.save(any(FinancialRecord.class))).thenReturn(record);

        dto.setCategory("Freelance");
        FinancialRecord result = recordService.updateRecord(1L, dto);

        verify(recordRepository).save(any(FinancialRecord.class));
        assertNotNull(result);
    }

    @Test
    void updateRecord_throwsWhenNotFound() {
        when(recordRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> recordService.updateRecord(99L, dto));
        assertEquals("Record not found with id: 99", ex.getMessage());
    }

    @Test
    void updateRecord_throwsWhenDeleted() {
        record.setDeleted(true);
        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> recordService.updateRecord(1L, dto));
        assertEquals("Cannot update a deleted record", ex.getMessage());
    }

    @Test
    void softDeleteRecord_setsDeletedFlag() {
        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(recordRepository.save(any(FinancialRecord.class))).thenReturn(record);

        recordService.softDeleteRecord(1L);

        assertTrue(record.isDeleted());
        verify(recordRepository).save(record);
    }

    @Test
    void softDeleteRecord_throwsWhenNotFound() {
        when(recordRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> recordService.softDeleteRecord(99L));
        assertEquals("Record not found with id: 99", ex.getMessage());
    }

    @Test
    void getTotalIncome_returnsZeroWhenNull() {
        when(recordRepository.sumByType(RecordType.INCOME)).thenReturn(null);

        BigDecimal result = recordService.getTotalIncome();

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void getNetBalance_returnsIncomeMinsExpenses() {
        when(recordRepository.sumByType(RecordType.INCOME)).thenReturn(new BigDecimal("5000"));
        when(recordRepository.sumByType(RecordType.EXPENSE)).thenReturn(new BigDecimal("1500"));

        BigDecimal result = recordService.getNetBalance();

        assertEquals(new BigDecimal("3500"), result);
    }
}