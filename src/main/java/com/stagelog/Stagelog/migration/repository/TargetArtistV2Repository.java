package com.stagelog.Stagelog.migration.repository;

import com.stagelog.Stagelog.migration.domain.TargetArtistV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TargetArtistV2Repository extends JpaRepository<TargetArtistV2, Long> {

    /**
     * 활성화된 타겟 아티스트 목록 조회
     */
    List<TargetArtistV2> findByIsActiveTrue();

    /**
     * 한글이름과 영어이름으로 조회
     */
    Optional<TargetArtistV2> findByArtistNameKrAndArtistNameEn(String artistNameKr, String artistNameEn);

    /**
     * 중복 체크
     */
    boolean existsByArtistNameKrAndArtistNameEn(String artistNameKr, String artistNameEn);

    /**
     * 활성화된 아티스트 수 조회
     */
    @Query("SELECT COUNT(t) FROM TargetArtistV2 t WHERE t.isActive = true")
    long countActiveArtists();
}
