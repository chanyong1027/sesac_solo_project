package com.stagelog.Stagelog.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== User 관련 (4001~4099) =====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 아이디입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "USER_003", "비밀번호가 일치하지 않습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "USER_004", "로그인 ID는 필수입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "USER_005", "올바르지 않은 이메일 형식입니다."),

    // ===== Review 관련 (4101~4199) =====
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_001", "존재하지 않는 리뷰입니다."),
    REVIEW_ACCESS_DENIED(HttpStatus.FORBIDDEN, "REVIEW_002", "해당 리뷰에 대한 권한이 없습니다."),

    // ===== Performance 관련 (4201~4299) =====
    PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "PERFORMANCE_001", "공연을 찾을 수 없습니다."),
    INTERESTED_PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "PERFORMANCE_002", "관심 등록되지 않은 공연입니다."),
    PERFORMANCE_DETAIL_NULL(HttpStatus.BAD_REQUEST, "PERFORMANCE_003", "공연 상세 정보가 null일 수 없습니다."),

    // ===== Artist 관련 (4301~4399) =====
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTIST_001", "아티스트를 찾을 수 없습니다."),

    // ===== Batch 관련 (5001~5099) =====
    BATCH_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH_001", "배치 실행 중 오류가 발생했습니다."),
    BATCH_INTERRUPTED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH_002", "배치 처리가 중단되었습니다."),

    // ===== 공통 (9000~9999) =====
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_002", "서버 내부 오류가 발생했습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "COMMON_003", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON_004", "접근 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
