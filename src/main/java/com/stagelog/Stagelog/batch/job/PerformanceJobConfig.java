package com.stagelog.Stagelog.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PerformanceJobConfig {

    private final JobRepository jobRepository;
    private final Step musicalFetchStep;

    @Bean
    public Job musicalFetchJob() {
        return new JobBuilder("musicalFetchJob", jobRepository)
                .start(musicalFetchStep)
                .build();
    }
}
