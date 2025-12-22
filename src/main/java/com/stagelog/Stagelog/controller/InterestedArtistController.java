package com.stagelog.Stagelog.controller;

import com.stagelog.Stagelog.domain.InterestedArtist;
import com.stagelog.Stagelog.service.InterestedArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interested-artist")
@RequiredArgsConstructor
public class InterestedArtistController {
    private final InterestedArtistService interestedArtistService;

    @PostMapping
    public ResponseEntity<InterestedArtist> save(@RequestBody InterestedArtist interestedArtist){
        return null;
    }
}
