package price.tracker.scheduler.job;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PasswordResetCodeCleaningJob extends QuartzJobBean {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) {
        try {
            Instant now = Instant.now();

            String query = "DELETE FROM password_reset_codes WHERE expiration < ?";
            jdbcTemplate.update(query, now.toEpochMilli());

            log.info("Expired password reset codes cleaned up successfully.");
        } catch (Exception e) {
            log.error("Error during password reset code cleanup: {}", e.getMessage());
        }
    }
}
