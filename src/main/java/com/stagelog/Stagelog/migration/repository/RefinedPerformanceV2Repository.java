package com.stagelog.Stagelog.migration.repository;

import com.stagelog.Stagelog.migration.domain.RefinedPerformanceV2;
import com.stagelog.Stagelog.migration.domain.TargetArtistV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefinedPerformanceV2Repository extends JpaRepository<RefinedPerformanceV2, Long> {

    /**
     * KOPIS ID로 조회
     */
    Optional<RefinedPerformanceV2> findByKopisId(String kopisId);

    /**
     * 특정 아티스트에 의해 매칭된 공연 목록
     */
    List<RefinedPerformanceV2> findByMatchedArtist(TargetArtistV2 matchedArtist);

    /**
     * 특정 아티스트 ID에 의해 매칭된 공연 수
     */
    @Query("SELECT COUNT(r) FROM RefinedPerformanceV2 r WHERE r.matchedArtist.id = :artistId")
    long countByMatchedArtistId(@Param("artistId") Long artistId);

    /**
     * 매칭 키워드별 그룹핑 통계
     */
    @Query("SELECT r.matchedKeyword, COUNT(r) FROM RefinedPerformanceV2 r " +
           "GROUP BY r.matchedKeyword ORDER BY COUNT(r) DESC")
    List<Object[]> getMatchedKeywordStatistics();

    /**
     * 필터 상태별 조회
     */
    List<RefinedPerformanceV2> findByFilterStatus(String filterStatus);
}
