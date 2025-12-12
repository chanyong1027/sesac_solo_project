package com.stagelog.Stagelog.repository;

import com.stagelog.Stagelog.domain.KopisPerformance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KopisPerformanceRepository extends JpaRepository<KopisPerformance,Long> {
    Optional<KopisPerformance> findByKopisId(String kopisId);
    boolean existsByKopisId(String kopisId);
    Page<KopisPerformance> findByHasDetailFalse(Pageable pageable);

}
