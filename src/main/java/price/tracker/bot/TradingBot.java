package price.tracker.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import price.tracker.coinbase.CoinbaseClient;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TradingBot {

    private final CoinbaseClient coinbaseClient;

    public void takeAction() {
        try {
            coinbaseClient.getOrders();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
