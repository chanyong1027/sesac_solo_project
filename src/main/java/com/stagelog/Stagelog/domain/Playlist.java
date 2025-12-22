package com.stagelog.Stagelog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Track> tracks = new ArrayList<>();

    public Playlist(String title) {
        this.title = title;
    }

    public void addTrack(Track track) {
        this.tracks.add(track);
        track.setPlaylist(this);
    }

    public Long getTotalDurationMs() {
        return tracks.stream()
                .mapToLong(Track::getDurationMs)
                .sum();
    }

    public String getFormattedTotalTime() {
        long totalMs = getTotalDurationMs();
        long minutes = (totalMs / 1000) / 60;
        long seconds = (totalMs / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public void updateTracks(List<Track> newTracks) {
        this.tracks.clear();
        if (newTracks != null) {
            for (Track track : newTracks) {
                this.addTrack(track);
            }
        }
    }

    public void updateTitle(String newTitle) {
        if (newTitle != null && !newTitle.isEmpty()) {
            this.title = newTitle;
        }
    }
}
