package com.stagelog.Stagelog.service;

import com.stagelog.Stagelog.domain.InterestedPerformance;
import com.stagelog.Stagelog.domain.RefinedPerformance;
import com.stagelog.Stagelog.domain.User;
import com.stagelog.Stagelog.dto.IPCreateRequest;
import com.stagelog.Stagelog.dto.IPCreateResponse;
import com.stagelog.Stagelog.dto.IPListResponse;
import com.stagelog.Stagelog.repository.InterestedPerformanceRepository;
import com.stagelog.Stagelog.repository.RefinedPerformanceRepository;
import com.stagelog.Stagelog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterestedPerformanceService {
    private final InterestedPerformanceRepository interestedPerformanceRepository;
    private final UserRepository userRepository;
    private final RefinedPerformanceRepository performanceRepository;

    @Transactional
    public IPCreateResponse create(Long userId, IPCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        RefinedPerformance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new IllegalArgumentException("공연을 찾을 수 없습니다."));

        InterestedPerformance ip = InterestedPerformance.create(user, performance);
        InterestedPerformance savedIp = interestedPerformanceRepository.save(ip);

        return new IPCreateResponse(savedIp.getId());
    }

    public List<IPListResponse> getMyList(Long userId) {
        return interestedPerformanceRepository.findAllByUserIdWithPerformance(userId).stream()
                .map(IPListResponse::new)
                .toList();
    }

    @Transactional
    public Long delete(Long userId, Long performanceId) {
        if (!interestedPerformanceRepository.existsByUserIdAndPerformanceId(userId, performanceId)) {
            throw new IllegalArgumentException("관심 등록되지 않은 공연입니다.");
        }
        interestedPerformanceRepository.deleteByUserIdAndPerformanceId(userId, performanceId);
        return performanceId;
    }
}
