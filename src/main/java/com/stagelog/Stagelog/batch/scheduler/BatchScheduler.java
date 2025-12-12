package com.stagelog.Stagelog.batch.scheduler;

import com.stagelog.Stagelog.batch.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final BatchService batchService;

    // 매일 새벽 2시 0분 0초에 실행
    @Scheduled(cron = "0 0 2 * * *")
    public void runDailyBatch() {
        batchService.runJob(LocalDate.now().toString());
    }
}
