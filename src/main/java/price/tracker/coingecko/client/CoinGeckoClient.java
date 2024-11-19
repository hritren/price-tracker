package price.tracker.coingecko.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import price.tracker.coingecko.client.exception.CoinGeckoClientException;
import price.tracker.coingecko.dto.CryptoCurrencyDTO;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CoinGeckoClient {

    private static final String IDS = "bitcoin, ethereum, solana, cardano, avalanche-2, chainlink, internet-computer";
    private static final String COINGECKO_REAL_TIME_ENDPOINT = String.format("https://api.coingecko.com/api/v3/coins/markets?ids=%s&vs_currency=usd", IDS);

    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate;

    public void getRealTimeCryptoData() {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<String> response = restTemplate.exchange(COINGECKO_REAL_TIME_ENDPOINT, HttpMethod.GET, entity, String.class);

            List<CryptoCurrencyDTO> cryptoCurrencyDTOs = createCryptoCurrencyDTO(response.getBody());
            saveCryptoCurrencyBatch(cryptoCurrencyDTOs);
        } catch (Exception e) {
            throw new CoinGeckoClientException("An error occurred calling the calling the coingecko API", e);
        }
    }

    private List<CryptoCurrencyDTO> createCryptoCurrencyDTO(String body) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(body, new TypeReference<List<CryptoCurrencyDTO>>() {
            });
        } catch (Exception e) {
            throw new CoinGeckoClientException("An error occurred parsing the response", e);
        }
    }

    private void saveCryptoCurrencyBatch(List<CryptoCurrencyDTO> cryptoCurrencyList) {
        String sql = "INSERT INTO CRYPTO_CURRENCY (crypto_id, name, symbol, price) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "name = VALUES(name), " +
                "symbol = VALUES(symbol), " +
                "price = VALUES(price)";

        List<Object[]> batchArgs = new ArrayList<>();

        for (CryptoCurrencyDTO cryptoCurrency : cryptoCurrencyList) {
            batchArgs.add(new Object[]{
                    cryptoCurrency.getCryptoId(),
                    cryptoCurrency.getName(),
                    cryptoCurrency.getSymbol(),
                    cryptoCurrency.getPrice()
            });
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }


    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x_cg_pro_api_key", getCoinGeckoApiKey());
        return headers;
    }

    private String getCoinGeckoApiKey() {
        return System.getenv().get("COIN_GECKO_API_KEY");
    }
}
