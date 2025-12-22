package com.stagelog.Stagelog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterestedArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_mapping_id")
    private ArtistMapping artistMapping;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private InterestedArtist(User user, ArtistMapping artistMapping) {
        this.user = user;
        this.artistMapping = artistMapping;
        this.createdAt = LocalDateTime.now();
    }

    public static InterestedArtist create(User user, ArtistMapping artistMapping) {
        return new InterestedArtist(user, artistMapping);
    }
}
