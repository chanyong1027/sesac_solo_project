package com.stagelog.Stagelog.service;

import com.stagelog.Stagelog.domain.KopisPerformance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KopisFilterService {
    private static final List<String> EXCLUDE_KEYWORDS = List.of(
            // 트로트
            "trot", "트로트",
            "송가인", "영탁", "이찬원", "진성", "주현미",
            "태진아", "남진", "나훈아", "송대관", "설운도",
            "현숙", "박상철", "홍진영", "박서진", "금잔디",
            "김희재", "장민호", "정동원", "김다현", "송가인",
            "남진", "조향조", "임영웅", "김호중", "박군", "장윤정",
            "홍자", "양지은", "미스트롯", "미스터트롯", "불타는트롯맨",
            "현역가왕",

            // K-POP/아이돌 (명백한 것만)
            "NCT", "BTS", "BLACKPINK", "SEVENTEEN",
            "Stray Kids", "스트레이키즈",
            "aespa", "에스파", "LE SSERAFIM", "르세라핌",
            "IVE", "아이브", "NewJeans", "뉴진스",
            "TOMORROW X TOGETHER", "ENHYPEN", "엔하이픈",
            "TWICE", "트와이스", "Red Velvet", "레드벨벳",
            "EXO", "엑소", "ITZY", "있지",
            "tripleS", "트리플에스",

            // 힙합 (명시적)
            "hiphop", "hip-hop", "HIPHOP", "HIP-HOP",
            "랩", "rap", "RAP", "래퍼",

            // ========== 클래식 ==========
            "오케스트라", "교향악단", "필하모닉", "심포니",
            "챔버", "리사이틀", "피아노 독주회",
            "오케스트라", "Orchestra", "필하모닉", "Philharmonic",
            "교향악단", "Symphony", "앙상블", "Ensemble",
            "리사이틀", "Recital", "독주회", "협주곡", "Concerto",
            "소프라노", "테너", "바리톤", "베이스", "성악",
            "합창단", "Choir", "국악", "판소리", "마당놀이",

            // 발라드/대형 보컬리스트
            "성시경", "박효신", "김범수", "나얼", "이수", "엠씨더맥스",
            "임창정", "김동률", "이적", "윤종신", "백지영", "거미",
            "에일리", "허각", "신용재", "바이브", "포맨", "다비치",
            "케이윌", "테이", "이승철", "신승훈", "변진섭", "조성모",
            "이문세", "이선희", "알리", "소향", "박정현", "이영현",
            "빅마마", "SG워너비", "V.O.S", "먼데이키즈",

            // ========== 국악 ==========
            "국악", "판소리", "가곡", "민요", "사물놀이",

            // ========== 지역 축제/행사 ==========
            "축제행사", "개막식", "폐막식", "시상식",
            "경연대회", "예선", "본선", "결선",
            "한마당", "올림픽",

            // 어린이
            "어린이날", "키즈"
    );

    public List<KopisPerformance> filterOutNonTarget(List<KopisPerformance> performances) {

        log.info("필터링 시작: 총 {}개", performances.size());

        // 정규표현식 패턴 생성
        String patternString = EXCLUDE_KEYWORDS.stream()
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));

        Pattern excludePattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);

        // 필터링
        List<KopisPerformance> filtered = performances.stream()
                .filter(p -> !shouldExclude(p.getTitle(), excludePattern))
                .collect(Collectors.toList());

        int excluded = performances.size() - filtered.size();
        double excludeRate = (excluded * 100.0) / performances.size();

        log.info("필터링 완료: {}개 제외 ({:.2f}%), {}개 남음 ({:.2f}%)",
                excluded, excludeRate,
                filtered.size(), 100 - excludeRate);

        return filtered;
    }

    private boolean shouldExclude(String title, Pattern pattern) {
        return pattern.matcher(title).find();
    }

    /**
     * 필터링 통계 반환
     */
    public FilterStatistics getStatistics(List<KopisPerformance> original,
                                          List<KopisPerformance> filtered) {

        int total = original.size();
        int remaining = filtered.size();
        int excluded = total - remaining;

        return FilterStatistics.builder()
                .totalCount(total)
                .remainingCount(remaining)
                .excludedCount(excluded)
                .excludeRate((excluded * 100.0) / total)
                .build();
    }

    /**
     * 필터링 통계 DTO
     */
    @lombok.Builder
    @lombok.Getter
    public static class FilterStatistics {
        private final int totalCount;
        private final int remainingCount;
        private final int excludedCount;
        private final double excludeRate;
    }
}
