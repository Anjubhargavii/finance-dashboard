package com.finance.dashboard.repository;

import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // --- Paginated versions (new) ---
    Page<FinancialRecord> findByDeletedFalse(Pageable pageable);

    Page<FinancialRecord> findByTypeAndDeletedFalse(RecordType type, Pageable pageable);

    Page<FinancialRecord> findByCategoryContainingIgnoreCaseAndDeletedFalse(String category, Pageable pageable);

    Page<FinancialRecord> findByDateBetweenAndDeletedFalse(LocalDate start, LocalDate end, Pageable pageable);

    // Search across category and notes (new)
    @Query("SELECT f FROM FinancialRecord f WHERE f.deleted = false AND " +
           "(LOWER(f.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.notes) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<FinancialRecord> searchByKeyword(String keyword, Pageable pageable);

    // --- Non-paginated versions (kept for dashboard aggregations) ---
    List<FinancialRecord> findByDeletedFalse();

    List<FinancialRecord> findByTypeAndDeletedFalse(RecordType type);

    List<FinancialRecord> findByCategoryAndDeletedFalse(String category);

    List<FinancialRecord> findByDateBetweenAndDeletedFalse(LocalDate start, LocalDate end);

    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.type = :type AND f.deleted = false")
    BigDecimal sumByType(RecordType type);

    @Query("SELECT f.category, SUM(f.amount) FROM FinancialRecord f WHERE f.deleted = false GROUP BY f.category")
    List<Object[]> sumByCategory();
}