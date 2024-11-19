package price.tracker.scheduler.job;

import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import price.tracker.coingecko.client.CoinGeckoClient;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RealTimeJob extends QuartzJobBean {

    private final CoinGeckoClient coinGeckoClient;

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) {
        coinGeckoClient.getRealTimeCryptoData();
    }
}
