package price.tracker.scheduler.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import price.tracker.coinbase.CoinbaseClient;
import price.tracker.coingecko.client.CoinGeckoClient;
import price.tracker.coingecko.client.exception.CoinGeckoClientException;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RealTimeJob extends QuartzJobBean {

    private final CoinGeckoClient coinGeckoClient;
    private final CoinbaseClient coinbaseClient;

    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) {
        try {
//            coinGeckoClient.getRealTimeCryptoData();
            coinbaseClient.getAccounts();
        } catch (CoinGeckoClientException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
