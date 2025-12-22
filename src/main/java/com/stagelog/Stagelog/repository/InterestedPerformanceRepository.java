package com.stagelog.Stagelog.repository;

import com.stagelog.Stagelog.domain.InterestedPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestedPerformanceRepository extends JpaRepository<InterestedPerformance, Long> {
    @Query("select ip from InterestedPerformance ip " +
            "join fetch ip.performance " +
            "where ip.user.id = :userId")
    List<InterestedPerformance> findAllByUserIdWithPerformance(@Param("userId") Long userId);
    void deleteByUserIdAndPerformanceId(Long userId, Long performanceId);

    boolean existsByUserIdAndPerformanceId(Long userId, Long performanceId);
}
