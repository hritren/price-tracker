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

    private double balance;
    private double cashInUSD;
    private double lastPositionEntryPrice;
    private List<Position> openPositions = new ArrayList<>();

    private final CoinbaseClient coinbaseClient;

}
