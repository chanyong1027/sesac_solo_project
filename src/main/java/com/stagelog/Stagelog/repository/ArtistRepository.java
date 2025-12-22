package com.stagelog.Stagelog.repository;

import com.stagelog.Stagelog.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist,Long> {
    boolean existsByName(String name);

    /**
     * 이름으로 검색 (부분 일치)
     */
    @Query("SELECT a FROM Artist a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Artist> searchByName(@Param("keyword") String keyword);

    /**
     * 아직 검색 안 한 아티스트 (전체)
     */
    @Query("SELECT a FROM Artist a WHERE a.searchStatus IS NULL")
    List<Artist> findBySearchStatusIsNull();

    /**
     * 아직 검색 안 한 아티스트 (배치)
     */
    @Query(value = "SELECT * FROM artists WHERE search_status IS NULL LIMIT :limit",
            nativeQuery = true)
    List<Artist> findBySearchStatusIsNullLimit(@Param("limit") int limit);

    /**
     * 검색 상태로 조회
     */
    @Query("SELECT a FROM Artist a WHERE a.searchStatus = :status")
    List<Artist> findBySearchStatus(@Param("status") String status);


    long countBySearchStatus(String status);
}
