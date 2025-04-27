package price.tracker.scheduler.config;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import price.tracker.scheduler.job.PasswordResetCodeCleaningJob;
import price.tracker.scheduler.job.RealTimeJob;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Configuration
public class QuartzConfig {

    private static final String REALTIME_CRON_EXPRESSION = "0 0/30 * * * ?";

    @Autowired
    private AutowiringSpringBeanJobFactory jobFactory;

    @Bean
    public JobDetail realTimeJobDetail() {
        return newJob(PasswordResetCodeCleaningJob.class)
                .withIdentity("resetCode", "cleaning")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger realTimeJobTrigger() {
        return newTrigger()
                .withIdentity("resetCodeTrigger", "cleaning")
                .withSchedule(cronSchedule(REALTIME_CRON_EXPRESSION))
                .forJob(realTimeJobDetail())
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactory() {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(jobFactory);
        factoryBean.setJobDetails(realTimeJobDetail());
        factoryBean.setTriggers(realTimeJobTrigger());
        return factoryBean;
    }
}
