package price.tracker.scheduler.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import price.tracker.bot.Strategy;
import price.tracker.coinbase.CoinbaseClient;

import java.sql.ResultSet;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BotActionJob extends QuartzJobBean {

    private final JdbcTemplate jdbcTemplate;
    private final CoinbaseClient coinbaseClient;

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) {
        try {
            String sql = "SELECT api_key_name, strategy FROM bots";

            jdbcTemplate.query(sql, (ResultSet rs) -> {
                while (rs.next()) {
                    String apiKeyName = rs.getString("api_key_name");
                    Strategy strategy = Strategy.valueOf(rs.getString("strategy"));

                    coinbaseClient.botAction(apiKeyName, strategy);
                }
            });
        } catch (Exception e) {
            log.error("Error during executing bot action: {}", e.getMessage());
        }
    }

}
