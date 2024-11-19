package price.tracker.bot;

import java.util.ArrayList;
import java.util.List;

public class TradingBot {

    private double balance;
    private double cashInUSD;
    private double lastPositionEntryPrice;

    private List<Position> openPositions = new ArrayList<>();
}
