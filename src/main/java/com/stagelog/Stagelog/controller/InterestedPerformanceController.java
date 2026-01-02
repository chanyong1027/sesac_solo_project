package com.stagelog.Stagelog.controller;

import com.stagelog.Stagelog.dto.IPCreateRequest;
import com.stagelog.Stagelog.dto.IPCreateResponse;
import com.stagelog.Stagelog.dto.IPListResponse;
import com.stagelog.Stagelog.global.security.service.CustomUserDetails;
import com.stagelog.Stagelog.service.InterestedPerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interested-performances")
@RequiredArgsConstructor
public class InterestedPerformanceController {
    private final InterestedPerformanceService interestedPerformanceService;

    //나중에 performanceId로 바꿔야함
    @PostMapping()
    public ResponseEntity<IPCreateResponse> createIP(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody IPCreateRequest ipCreateRequest) {
        Long userId = userDetails.getUser().getId();

        interestedPerformanceService.create(userId, ipCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<IPListResponse>> getIP(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        ;
        return ResponseEntity.ok(interestedPerformanceService.getMyList(userId));
    }

    @DeleteMapping("/{performanceId}")
    public ResponseEntity<Long> deleteIP(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long performanceId) {
        Long userId = userDetails.getUser().getId();
        Long deleteInterestedPerformanceId = interestedPerformanceService.delete(userId, performanceId);
        return ResponseEntity.ok(deleteInterestedPerformanceId);
    }
}
