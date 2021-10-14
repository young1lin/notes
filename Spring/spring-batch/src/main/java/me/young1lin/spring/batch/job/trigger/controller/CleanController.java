package me.young1lin.spring.batch.job.trigger.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/8/7 上午10:12
 * @version 1.0
 */
@RestController
@RequestMapping("/clean/{jobName}")
public class CleanController {

	private final JobLauncher jobLauncher;

	private final Map<String, Job> cleanJobMap;


	public CleanController(JobLauncher jobLauncher, Map<String, Job> cleanJobMap) {
		this.jobLauncher = jobLauncher;
		this.cleanJobMap = cleanJobMap;
	}


	@GetMapping
	public ResponseEntity<?> clean(@PathVariable("jobName") String jobName)
			throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
			JobRestartException, JobInstanceAlreadyCompleteException {
		Job job = cleanJobMap.get(jobName);
		jobLauncher.run(job,
				new JobParametersBuilder()
						.addDate("start-time", new Date())
						.toJobParameters());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}