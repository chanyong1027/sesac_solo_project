package com.stagelog.Stagelog.batch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchService {
    private final JobLauncher jobLauncher;
    private final Job performanceFetchJob;

    public void runJob(String startDate){
        try{
            String dateParam = startDate;

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("startDate", dateParam)
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(performanceFetchJob, jobParameters);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
