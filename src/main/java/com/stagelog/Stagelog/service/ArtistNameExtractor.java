package com.stagelog.Stagelog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ArtistNameExtractor {

    // 페스티벌 패턴
    private static final Pattern FESTIVAL = Pattern.compile(
            "페스티벌|페스타|festival|festa",
            Pattern.CASE_INSENSITIVE
    );

    // 콘서트/투어 패턴 (탐욕적이지 않게)
    private static final Pattern CONCERT = Pattern.compile(
            "^(.+?)\\s*(?:콘서트|concert|투어|tour|공연)",
            Pattern.CASE_INSENSITIVE
    );

    // 불용어 - 콘서트 수식어
    private static final Set<String> CONCERT_MODIFIERS = Set.of(
            "단독", "전국", "특별", "기념", "생애", "첫", "마지막",
            "단독콘서트", "전국투어", "특별공연"
    );

    // 불용어 - 형용사/일반명사
    private static final Set<String> COMMON_WORDS = Set.of(
            // 형용사
            "아름다운", "특별한", "행복한", "사랑의", "멋진", "새로운",
            "신나는", "즐거운", "따뜻한", "설레는",

            // 계절/시간
            "봄", "여름", "가을", "겨울", "봄밤", "여름밤", "가을밤", "겨울밤",
            "크리스마스", "연말", "신년", "새해",

            // 일반명사
            "앵콜", "encore", "오픈", "open", "프리미어", "premiere",
            "스페셜", "special",

            // 행사성
            "콘서트", "concert", "공연", "투어", "tour", "live",
            "쇼", "show", "쇼케이스", "showcase", "팬콘", "fancon"
    );

    // 무조건 제외할 시작 키워드
    private static final Set<String> INVALID_STARTS = Set.of(
            "제", "현대카드", "curated", "vol", "Vol", "VOL",
            "ep", "EP", "part", "Part", "PART"
    );

    public List<String> extract(String title){
        if (!StringUtils.hasText(title)) {
            return Collections.emptyList();
        }

        // 1. 전처리
        String cleaned = preprocess(title);

        if (!StringUtils.hasText(cleaned)) {
            log.debug("전처리 후 빈 문자열: {}", title);
            return Collections.emptyList();
        }

        // 2. 페스티벌 체크
        if (FESTIVAL.matcher(cleaned).find()) {
            log.debug("페스티벌 스킵: {}", title);
            return Collections.emptyList();
        }

        // 3. 패턴별 추출 시도
        List<String> result;

        // 패턴 1: "아티스트 + 콘서트/투어"
        result = extractFromConcertPattern(cleaned);
        if (!result.isEmpty()) {
            log.debug("콘서트 패턴 추출: {} → {}", title, result);
            return result;
        }

        // 패턴 2: "&" 또는 "×" 분리
        result = extractFromCollaboration(cleaned);
        if (!result.isEmpty()) {
            log.debug("콜라보 패턴 추출: {} → {}", title, result);
            return result;
        }

        // 패턴 3: 첫 단어 (fallback)
        result = extractFirstWord(cleaned);
        if (!result.isEmpty()) {
            log.debug("첫 단어 추출: {} → {}", title, result);
            return result;
        }

        log.debug("추출 실패: {}", title);
        return Collections.emptyList();
    }

    /**
     * 전처리
     */
    private String preprocess(String title) {
        String result = title;

        // 1. 대괄호 제거 (지역 정보 등)
        // "[서울]", "[대구]", "[HNK]" 등
        result = result.replaceAll("\\[.*?\\]", " ");

        // 2. 소괄호 제거 (부가 정보)
        // "(1.5.)", "(앵콜)", "(비오)" 등
        result = result.replaceAll("\\(.*?\\)", " ");

        // 3. 날짜 형식 제거
        // "2024.12.25", "12.25", "01.05" 등
        result = result.replaceAll("\\d{4}\\.\\d{1,2}\\.\\d{1,2}", " ");
        result = result.replaceAll("\\d{1,2}\\.\\d{1,2}\\.?", " ");

        // 4. 연도 제거 (단독으로 있는 경우)
        result = result.replaceAll("\\b\\d{4}\\b", " ");

        // 5. "제N회" 제거
        result = result.replaceAll("제\\d+회", " ");

        // 6. Vol/EP/Part 제거
        result = result.replaceAll("\\b(?i)vol\\.?\\s*\\d+", " ");
        result = result.replaceAll("\\b(?i)ep\\.?\\s*\\d+", " ");
        result = result.replaceAll("\\b(?i)part\\.?\\s*\\d+", " ");

        // 7. 다중 공백 정리
        result = result.replaceAll("\\s+", " ").trim();

        return result;
    }

    /**
     * 패턴 1: "아티스트 콘서트/투어"
     */
    private List<String> extractFromConcertPattern(String title) {

        Matcher matcher = CONCERT.matcher(title);
        if (!matcher.find()) {
            return Collections.emptyList();
        }

        String candidate = matcher.group(1).trim();

        for (String modifier : CONCERT_MODIFIERS) {
            candidate = candidate.replaceAll("(?i)" + Pattern.quote(modifier), " ");
        }

        candidate = candidate.trim();

        candidate = removeSupport(candidate);

        if (isValidArtistName(candidate)) {
            return List.of(candidate);
        }

        return Collections.emptyList();
    }

    /**
     * 패턴 2: "&" 또는 "×" 분리
     */
    private List<String> extractFromCollaboration(String title) {

        // & 또는 × 또는 X 체크
        if (!title.matches(".*[&×].*") && !title.matches(".*\\sX\\s.*")) {
            return Collections.emptyList();
        }

        // 분리
        String[] parts = title.split("[&×]|\\sX\\s");

        List<String> artists = new ArrayList<>();

        for (String part : parts) {
            String[] subParts = part.split(",");

            for (String subPart : subParts){
                // 콜론/쉼표 앞 부분만
                String cleaned = part.split("[,:]")[0].trim();

                // 콘서트 등 키워드 제거
                cleaned = cleaned.replaceAll("(?i)(콘서트|concert|투어|tour|공연|음악회)", " ");
                cleaned = cleaned.trim();

                // with/feat 이후는 제거
                cleaned = removeSupport(cleaned);

                if (isValidArtistName(cleaned)) {
                    artists.add(cleaned);
                }
            }
        }

        return artists;
    }

    /**
     * 패턴 3: 첫 단어 (fallback)
     */
    private List<String> extractFirstWord(String title) {

        // 콜론/쉼표 앞 부분
        String firstPart = title.split("[,:]")[0].trim();

        // with/feat 이전 부분
        firstPart = removeSupport(firstPart);

        // 공백으로 분리
        String[] words = firstPart.split("\\s+");

        if (words.length == 0) {
            return Collections.emptyList();
        }

        String candidate = words[0].trim();

        // 유효성 검사 (더 엄격하게)
        if (isValidArtistName(candidate) && !isCommonWord(candidate)) {
            return List.of(candidate);
        }

        return Collections.emptyList();
    }

    /**
     * with/feat 이후 제거 (서포트 아티스트)
     */
    private String removeSupport(String text) {
        // "with", "feat", "ft" 이후는 서포트 아티스트
        String[] parts = text.split("\\b(?i)(with|feat\\.?|ft\\.?)\\b");
        return parts[0].trim();
    }

    /**
     * 유효한 아티스트명인지 검증
     */
    private boolean isValidArtistName(String name) {

        if (!StringUtils.hasText(name)) {
            return false;
        }

        // 최소 길이
        if (name.length() < 2) {
            return false;
        }

        // 순수 숫자 (단, 밴드명은 허용: 10CM, 2AM 등)
        // 숫자만 있으면서 알파벳이 없으면 제외
        if (name.matches("^\\d+$")) {
            return false;
        }

        // 무효한 시작 키워드
        for (String invalid : INVALID_STARTS) {
            if (name.toLowerCase().startsWith(invalid.toLowerCase())) {
                return false;
            }
        }

        // 형용사/일반명사 체크
        if (isCommonWord(name)) {
            return false;
        }

        return true;
    }

    /**
     * 일반명사/형용사인지 체크
     */
    private boolean isCommonWord(String word) {
        String lower = word.toLowerCase();

        for (String common : COMMON_WORDS) {
            if (lower.equals(common.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
