package com.stagelog.Stagelog.api;

import com.stagelog.Stagelog.batch.dto.KopisPerformanceApiDto;
import com.stagelog.Stagelog.dto.KopisPerformanceDetailResponseDto;
import com.stagelog.Stagelog.dto.KopisPerformanceResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class KopisApi {

    private static final int ROWS_PER_PAGE = 100;
    //private static final String PRF_STATE_RUNNING = "01";
    private static final String SEOUL_CODE = "11";
    private static final String CATEGORY_MUSICAL = "CCCD";
    private static final String BASE_URL = "http://www.kopis.or.kr";

    @Value("${external.kopis}")
    private String apiKey;
    private final RestClient restClient;

    public KopisApi(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl(BASE_URL)
                .build();
    }

    public List<KopisPerformanceApiDto> fetchPerformances(int cpage){

        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate now = LocalDate.now();

            String startDate = now.plusMonths(1).format(formatter);
            String endDate = now.plusMonths(2).format(formatter);

            String uri = UriComponentsBuilder
                    .fromPath("/openApi/restful/pblprfr")
                    .queryParam("service", apiKey)
                    .queryParam("stdate", startDate)
                    .queryParam("eddate", endDate)
                    .queryParam("cpage", cpage)
                    .queryParam("rows", ROWS_PER_PAGE)
                    .queryParam("signgucode", SEOUL_CODE)
                    .queryParam("shcate", CATEGORY_MUSICAL)
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
            log.error("Kopis api 호출 중 오류 발생: cpage={}", cpage, e);
            return Collections.emptyList();
        }
    }

    public KopisPerformanceDetailResponseDto fetchMusicalDetail(String kopisId) {

        try {
            String uri = UriComponentsBuilder
                    .fromPath("/openApi/restful/pblprfr/{mt20id}")
                    .queryParam("service", apiKey)
                    .buildAndExpand(kopisId)
                    .toString();

            KopisPerformanceDetailResponseDto response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(KopisPerformanceDetailResponseDto.class);

            if (response == null || response.getDetail() == null || response.getDetail().isEmpty()) {
                return null;
            }

            return response.getDetail().get(0);

        } catch (RestClientException e) {
            log.error("KOPIS 상세 API 호출 및 파싱 중 오류 발생: mt20id={}", kopisId, e);
            return null;
        }
    }
}
