package com.stagelog.Stagelog.repository;

import com.stagelog.Stagelog.domain.RefinedPerformance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefinedPerformanceRepository extends JpaRepository<RefinedPerformance, Long> {
    /**
     * artist_name_kr이 null인 공연
     */
    List<RefinedPerformance> findByArtistNameKrIsNull(Sort sort);

    @Query("SELECT p FROM RefinedPerformance p WHERE " +
            "p.artistNameKr IS NOT NULL")
    List<RefinedPerformance> findByArtistNameKr();

    /**
     * artist_name_kr이 있는 공연 개수
     */
    long countByArtistNameKrIsNotNull();

    /**
     * artist_name_kr이 없는 공연 개수
     */
    long countByArtistNameKrIsNull();

    /**
     * 제목 또는 아티스트명으로 검색
     */
    @Query("SELECT p FROM RefinedPerformance p WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.artistNameKr) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.artistNameEn) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<RefinedPerformance> searchByTitleOrArtist(@Param("keyword") String keyword);

    /**
     * KOPIS ID로 조회
     */
    Optional<RefinedPerformance> findByKopisId(String kopisId);

    Page<RefinedPerformance> findByIsFestival(boolean festival, Pageable pageable);

    @Query("SELECT p FROM RefinedPerformance p WHERE p.title LIKE %:keyword% OR p.artistNameKr LIKE %:keyword% OR p.artistNameEn LIKE %:keyword%")
    Page<RefinedPerformance> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT p FROM RefinedPerformance p WHERE p.startDate <= :endOfMonth AND p.endDate >= :startOfMonth")
    List<RefinedPerformance> findPerformancesInMonth(
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("endOfMonth")LocalDate endOfMonth);
}
