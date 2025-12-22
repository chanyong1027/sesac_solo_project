package com.stagelog.Stagelog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ArtistMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameKr;

    private String nameEn;

    @Column(columnDefinition = "TEXT")
    private String genres;

    private String alias;

    @OneToMany(mappedBy = "artistMapping")
    private List<RefinedPerformance> performances = new ArrayList<>();

    public static ArtistMapping create(String nameKr, String nameEn, String genres) {
        ArtistMapping artist = new ArtistMapping();
        artist.nameKr = nameKr;
        artist.nameEn = nameEn;
        artist.genres = genres;
        return artist;
    }

}
