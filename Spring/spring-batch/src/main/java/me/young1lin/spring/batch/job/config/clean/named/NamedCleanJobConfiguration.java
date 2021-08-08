package me.young1lin.spring.batch.job.config.clean.named;

import me.young1lin.spring.batch.job.config.clean.CleanJobNameSpace;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/8/7 上午10:21
 * @version 1.0
 */
@Configuration
public class NamedCleanJobConfiguration {

	public static final String PERSON_CLEAN_NAME = CleanJobNameSpace.CLEAN_JOB_PREFIX +
			"person-job";

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;


	public NamedCleanJobConfiguration(JobBuilderFactory jobBuilderFactory,
			StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	@Bean(PERSON_CLEAN_NAME)
	public Job personJob() {
		return jobBuilderFactory.get(PERSON_CLEAN_NAME)
				// 配置为不可重启的 Job
				.preventRestart()
				.start(personStep())
				.build();
	}

	@Bean
	public Step personStep() {
		return stepBuilderFactory.get(PERSON_CLEAN_NAME + "-step")
				.tasklet((stepContribution, chunkContext) -> {
					System.out.println(chunkContext.getStepContext().getJobParameters());
					Thread.sleep(5000);
					return RepeatStatus.FINISHED;
				})
				.build();
	}

}
