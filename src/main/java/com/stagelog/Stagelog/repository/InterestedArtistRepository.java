package com.stagelog.Stagelog.repository;

import com.stagelog.Stagelog.domain.InterestedArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestedArtistRepository extends JpaRepository<InterestedArtist,Long> {
}
