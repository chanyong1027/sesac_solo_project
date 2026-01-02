package com.stagelog.Stagelog.migration.service;

import com.stagelog.Stagelog.migration.domain.TargetArtistV2;
import com.stagelog.Stagelog.migration.dto.TargetArtistUploadResponse;
import com.stagelog.Stagelog.migration.repository.TargetArtistV2Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TargetArtistService {

    private final TargetArtistV2Repository targetArtistRepository;

    /**
     * 엑셀 파일에서 아티스트 정보 읽어서 일괄 등록
     */
    public TargetArtistUploadResponse uploadFromExcel(MultipartFile file) throws IOException {

        log.info("엑셀 파일 업로드 시작: {}", file.getOriginalFilename());

        List<ArtistExcelRow> artistRows = readArtistsFromExcel(file);
        log.info("엑셀에서 읽은 아티스트 수: {}명", artistRows.size());

        List<String> added = new ArrayList<>();
        List<String> duplicated = new ArrayList<>();

        for (ArtistExcelRow row : artistRows) {
            // 중복 체크
            if (targetArtistRepository.existsByArtistNameKrAndArtistNameEn(
                    row.artistNameKr, row.artistNameEn)) {
                duplicated.add(row.artistNameKr);
                log.debug("중복 아티스트: {} ({})", row.artistNameKr, row.artistNameEn);
            } else {
                TargetArtistV2 artist = TargetArtistV2.create(
                    row.artistNameKr,
                    row.artistNameEn,
                    row.alias
                );
                targetArtistRepository.save(artist);
                added.add(row.artistNameKr);
                log.debug("추가된 아티스트: {} ({})", row.artistNameKr, row.artistNameEn);
            }
        }

        log.info("업로드 완료 - 추가: {}명, 중복: {}명", added.size(), duplicated.size());

        return TargetArtistUploadResponse.builder()
            .totalCount(artistRows.size())
            .newCount(added.size())
            .duplicateCount(duplicated.size())
            .addedArtists(added)
            .duplicatedArtists(duplicated)
            .build();
    }

    /**
     * 엑셀 파일에서 아티스트 정보 추출
     *
     * 엑셀 형식:
     * | id | artist_name_kr | artist_name_en | alias          |
     * |----|----------------|----------------|----------------|
     * | 1  | 잔나비         | jannabi        | 잔나비|JANNABI |
     */
    private List<ArtistExcelRow> readArtistsFromExcel(MultipartFile file) throws IOException {
        List<ArtistExcelRow> artists = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 첫 번째 행은 헤더이므로 건너뜀
            boolean isFirstRow = true;

            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                // 각 컬럼 읽기
                String artistNameKr = getCellValueAsString(row.getCell(1)); // B열
                String artistNameEn = getCellValueAsString(row.getCell(2)); // C열
                String alias = getCellValueAsString(row.getCell(3));        // D열

                // 필수 필드 검증
                if (artistNameKr != null && !artistNameKr.isEmpty() &&
                    artistNameEn != null && !artistNameEn.isEmpty()) {

                    artists.add(new ArtistExcelRow(
                        artistNameKr.trim(),
                        artistNameEn.trim(),
                        (alias != null) ? alias.trim() : ""
                    ));
                }
            }
        }

        return artists;
    }

    /**
     * 셀 값을 문자열로 변환
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }

    /**
     * 엑셀 행 데이터 DTO
     */
    private record ArtistExcelRow(
        String artistNameKr,
        String artistNameEn,
        String alias
    ) {}

    /**
     * 활성화된 타겟 아티스트 목록 조회
     */
    @Transactional(readOnly = true)
    public List<TargetArtistV2> getAllActive() {
        return targetArtistRepository.findByIsActiveTrue();
    }

    /**
     * 전체 타겟 아티스트 목록 조회
     */
    @Transactional(readOnly = true)
    public List<TargetArtistV2> getAll() {
        return targetArtistRepository.findAll();
    }

    /**
     * 타겟 아티스트 비활성화
     */
    public void deactivate(Long id) {
        TargetArtistV2 artist = targetArtistRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("아티스트를 찾을 수 없습니다: " + id));
        artist.deactivate();
    }

    /**
     * 타겟 아티스트 활성화
     */
    public void activate(Long id) {
        TargetArtistV2 artist = targetArtistRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("아티스트를 찾을 수 없습니다: " + id));
        artist.activate();
    }

    /**
     * 전체 삭제 (테스트용)
     */
    public void deleteAll() {
        targetArtistRepository.deleteAll();
        log.info("모든 타겟 아티스트 삭제 완료");
    }
}
