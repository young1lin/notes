package me.young1lin.spring.batch.job.config.clean.named;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.young1lin.spring.batch.job.config.clean.CleanJobNameSpace;


/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/8/7 上午10:21
 * @version 1.0
 */
@Configuration
public class NamedCleanJobConfiguration {

	public static final String PERSON_CLEAN_NAME = CleanJobNameSpace.CLEAN_JOB_PREFIX
			+ "person-job";

	public static final String PERSON_MARKET_NAME = CleanJobNameSpace.MARKET_JOB_PREFIX
			+ "person-job";

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;


	public NamedCleanJobConfiguration(final JobBuilderFactory jobBuilderFactory,
			final StepBuilderFactory stepBuilderFactory) {
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

	@Bean(PERSON_MARKET_NAME)
	public Job marketJob() {
		return jobBuilderFactory.get(PERSON_MARKET_NAME)
				.start(personMarketStep())
				.build();
	}

	@Bean
	public Step personMarketStep() {
		return stepBuilderFactory.get(PERSON_MARKET_NAME + "-step")
				// Spring Batch 框架规定 Job 在使用相同的识别性参数时，只能成功执行一次。这是无法改变的，但是这个不适用于 Step
				// 开启这个设置，就是为了在重启这个 Step 时，可以重新启动
				.allowStartIfComplete(true)
				// 可以重试两次
				.startLimit(2)
				.tasklet((stepContribution, chunkContext) -> {
					System.out.println(chunkContext.getStepContext().getJobParameters());
					return RepeatStatus.FINISHED;
				})
				.build();
	}

}
