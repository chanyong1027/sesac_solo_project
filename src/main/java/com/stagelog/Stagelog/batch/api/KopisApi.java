package com.stagelog.Stagelog.batch.api;

import com.stagelog.Stagelog.batch.dto.KopisPerformanceApiDto;
import com.stagelog.Stagelog.dto.KopisPerformanceDetailResponseDto;
import com.stagelog.Stagelog.dto.KopisPerformanceResponseDto;
import com.stagelog.Stagelog.dto.RealKopisPerformanceDetailResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@Component
public class KopisApi {

    // KOPIS API 한 번 호출 시 최대 100개 조회 가능
    private static final int ROWS_PER_PAGE = 100;
    private static final String BASE_URL = "http://www.kopis.or.kr";

    @Value("${external.kopis}")
    private String apiKey;
    private final RestClient restClient;

    public KopisApi(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl(BASE_URL)
                .build();
    }

    public List<KopisPerformanceApiDto> fetchPerformances(String startDate, String endDate, int currentPage, String category) {

        try{

            String uri = UriComponentsBuilder
                    .fromPath("/openApi/restful/pblprfr")
                    .queryParam("service", apiKey)
                    .queryParam("stdate", startDate)
                    .queryParam("eddate", endDate)
                    .queryParam("cpage", currentPage)
                    .queryParam("rows", ROWS_PER_PAGE)
                    .queryParam("shcate", category)
                    .build(true)
                    .toString();

            KopisPerformanceResponseDto response= restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(KopisPerformanceResponseDto.class);

            if (response == null || response.getPerformances() == null) {
                return Collections.emptyList();
            }

            return response.getPerformances();
        }
        catch (RestClientException e){
            log.error("Kopis api 호출 중 오류 발생: cpage={}", currentPage, e);
            return Collections.emptyList();
        }
    }

    public RealKopisPerformanceDetailResponseDto fetchMusicalDetail(String kopisId) {
        try {
            String uri = UriComponentsBuilder
                    .fromPath("/openApi/restful/pblprfr/{mt20id}")
                    .queryParam("service", apiKey)
                    .buildAndExpand(kopisId)
                    .toString();

            //log.debug("KOPIS 상세 정보 조회 시작: kopisId={}", kopisId);

            KopisPerformanceDetailResponseDto response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(KopisPerformanceDetailResponseDto.class);

            if (response == null || response.getDetails() == null || response.getDetails().isEmpty()) {
                log.warn("KOPIS API 응답이 null입니다: kopisId={}", kopisId);
                return null;
            }
            RealKopisPerformanceDetailResponseDto detail = response.getFirstDetail();

            if (detail == null) {
                log.warn("상세 정보가 비어있습니다: kopisId={}", kopisId);
                return null;
            }

            //log.debug("상세 정보 조회 성공: kopisId={}, title={}", kopisId, detail.getPrfnm());
            return detail;

        } catch (RestClientException e) {
            log.error("KOPIS 상세 API 호출 및 파싱 중 오류 발생: mt20id={}", kopisId, e);
            return null;
        }
    }
}
