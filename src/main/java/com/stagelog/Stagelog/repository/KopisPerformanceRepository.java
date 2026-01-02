package com.stagelog.Stagelog.repository;

import com.stagelog.Stagelog.domain.KopisPerformance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KopisPerformanceRepository extends JpaRepository<KopisPerformance,Long> {
    Optional<KopisPerformance> findByKopisId(String kopisId);

    @Query("SELECT k FROM KopisPerformance k WHERE k.hasDetail = true AND k.isRefined = false")
    List<KopisPerformance> findByHasDetailTrueAndIsRefinedFalse();

    @Query("SELECT k FROM KopisPerformance k WHERE k.hasDetail = true")
    List<KopisPerformance> findByHasDetailTrue();
}
