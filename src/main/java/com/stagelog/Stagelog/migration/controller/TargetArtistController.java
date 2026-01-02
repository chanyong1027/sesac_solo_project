package com.stagelog.Stagelog.migration.controller;

import com.stagelog.Stagelog.migration.domain.TargetArtistV2;
import com.stagelog.Stagelog.migration.dto.TargetArtistUploadResponse;
import com.stagelog.Stagelog.migration.service.TargetArtistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/migration/target-artists")
@RequiredArgsConstructor
@Slf4j
public class TargetArtistController {

    private final TargetArtistService targetArtistService;

    /**
     * 엑셀 파일 업로드
     */
    @PostMapping("/upload")
    public ResponseEntity<TargetArtistUploadResponse> uploadExcel(
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("엑셀 업로드 요청: {}", file.getOriginalFilename());
        TargetArtistUploadResponse response = targetArtistService.uploadFromExcel(file);
        return ResponseEntity.ok(response);
    }

    /**
     * 활성화된 타겟 아티스트 목록 조회
     */
    @GetMapping("/active")
    public ResponseEntity<List<TargetArtistV2>> getAllActiveArtists() {
        return ResponseEntity.ok(targetArtistService.getAllActive());
    }

    /**
     * 전체 타겟 아티스트 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<TargetArtistV2>> getAllArtists() {
        return ResponseEntity.ok(targetArtistService.getAll());
    }

    /**
     * 타겟 아티스트 비활성화
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        targetArtistService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 타겟 아티스트 활성화
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        targetArtistService.activate(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 전체 삭제 (테스트용)
     */
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll() {
        targetArtistService.deleteAll();
        return ResponseEntity.ok().build();
    }
}
