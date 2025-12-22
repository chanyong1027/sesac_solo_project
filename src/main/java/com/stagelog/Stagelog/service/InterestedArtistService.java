package com.stagelog.Stagelog.service;

import com.stagelog.Stagelog.repository.InterestedArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestedArtistService {
    private final InterestedArtistRepository interestedArtistRepository;
}
