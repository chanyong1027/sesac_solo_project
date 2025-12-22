package com.stagelog.Stagelog.repository;

import com.stagelog.Stagelog.domain.ArtistMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistMappingRepository extends JpaRepository<ArtistMapping, Long> {
}
