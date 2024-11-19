package price.tracker.coingecko.client.exception;

public class CoinGeckoClientException extends RuntimeException {

    public CoinGeckoClientException(String message) {
        super(message);
    }

    public CoinGeckoClientException(String message, Exception exception) {
        super(message, exception);
    }
}
