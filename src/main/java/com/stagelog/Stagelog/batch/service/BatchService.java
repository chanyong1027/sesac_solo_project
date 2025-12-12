package com.stagelog.Stagelog.batch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchService {
    private final JobLauncher jobLauncher;
    private final ApplicationContext applicationContext;

    public void runJob(String jobName, String startDate){
        try{
            Job job = applicationContext.getBean(jobName,  Job.class);

            JobParametersBuilder paramsBuilder = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()); // 중복 실행 방지용

            if (startDate != null) {
                paramsBuilder.addString("startDate", startDate);
            }

            jobLauncher.run(job, paramsBuilder.toJobParameters());
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("배치 실행 중 오류 발생: " + jobName);
        }
    }
}
