package com.stagelog.Stagelog.migration.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TargetArtistUploadResponse {
    private int totalCount;       // 총 아티스트 수
    private int newCount;         // 새로 추가된 수
    private int duplicateCount;   // 중복된 수
    private List<String> addedArtists;      // 추가된 아티스트 목록 (한글이름)
    private List<String> duplicatedArtists; // 중복된 아티스트 목록 (한글이름)
}
