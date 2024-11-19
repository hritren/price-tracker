package price.tracker.coingecko.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import price.tracker.coingecko.client.exception.CoinGeckoClientException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoinGeckoClientTest {

    private static final String IDS = "bitcoin, ethereum, solana, cardano, avalanche-2, chainlink, internet-computer";
    private static final String COINGECKO_REAL_TIME_ENDPOINT = String.format("https://api.coingecko.com/api/v3/coins/markets?ids=%s&vs_currency=usd", IDS);
    private static final String BITCOIN_JSON_OBJECT = """
            {
                    "id": "bitcoin",
                    "symbol": "btc",
                    "name": "Bitcoin",
                    "current_price": 93239,
                    "market_cap": 1839776112611
                }
            """;
    private static final String RESPONSE_BODY = """
            [
                {
                    "id": "bitcoin",
                    "symbol": "btc",
                    "name": "Bitcoin",
                    "current_price": 93239,
                    "market_cap": 1839776112611
                },
                {
                    "id": "ethereum",
                    "symbol": "eth",
                    "name": "Ethereum",
                    "current_price": 3130.87,
                    "market_cap": 376057058512
                }
            ]
            """;
    private static final String SQL = "INSERT INTO CRYPTO_CURRENCY (crypto_id, name, symbol, price) " +
            "VALUES (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "name = VALUES(name), " +
            "symbol = VALUES(symbol), " +
            "price = VALUES(price)";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResponseEntity<String> responseEntity;

    @Captor
    private ArgumentCaptor<List<Object[]>> batchArgsCaptor;

    @InjectMocks
    private CoinGeckoClient client;

    @Test
    void testGetRealTimeCryptoData() {
        when(restTemplate.exchange(eq(COINGECKO_REAL_TIME_ENDPOINT), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(RESPONSE_BODY);

        client.getRealTimeCryptoData();

        verify(jdbcTemplate, times(1)).batchUpdate(eq(SQL), batchArgsCaptor.capture());

        List<Object[]> capturedArgs = batchArgsCaptor.getValue();
        assertEquals(2, capturedArgs.size());

        assertEquals("bitcoin", capturedArgs.get(0)[0]);
        assertEquals("Bitcoin", capturedArgs.get(0)[1]);
        assertEquals("btc", capturedArgs.get(0)[2]);
        assertEquals(93239.0, capturedArgs.get(0)[3]);

        assertEquals("ethereum", capturedArgs.get(1)[0]);
        assertEquals("Ethereum", capturedArgs.get(1)[1]);
        assertEquals("eth", capturedArgs.get(1)[2]);
        assertEquals(3130.87, capturedArgs.get(1)[3]);
    }

    @Test
    void testGetRealTimeCryptoDataThrowsException() {
        when(restTemplate.exchange(eq(COINGECKO_REAL_TIME_ENDPOINT), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(BITCOIN_JSON_OBJECT);

        assertThrows(CoinGeckoClientException.class, () -> client.getRealTimeCryptoData());
    }
}
