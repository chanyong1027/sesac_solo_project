package com.stagelog.Stagelog.batch.job;

import com.stagelog.Stagelog.batch.dto.KopisPerformanceApiDto;
import com.stagelog.Stagelog.batch.processor.PerformanceItemProcessor;
import com.stagelog.Stagelog.batch.reader.PerformanceItemReader;
import com.stagelog.Stagelog.batch.writer.PerformanceItemWriter;
import com.stagelog.Stagelog.domain.KopisPerformance;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class PerformanceStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PerformanceItemReader performanceItemReader;
    private final PerformanceItemProcessor performanceItemProcessor;
    private final PerformanceItemWriter performanceItemWriter;

    private static final int CHUNK_SIZE = 100;

    @Bean
    public Step musicalFetchStep() {
        return new StepBuilder("musicalFetchStep", jobRepository)
                .<KopisPerformanceApiDto, KopisPerformance>chunk(CHUNK_SIZE, transactionManager)
                .reader(performanceItemReader)
                .processor(performanceItemProcessor)
                .writer(performanceItemWriter)
                .build();
    }
}
