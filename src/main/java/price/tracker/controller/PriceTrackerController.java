package price.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import price.tracker.coinbase.CoinbaseClient;
import price.tracker.coingecko.dto.CryptoCurrencyDTO;
import price.tracker.encryption.EncryptionService;

@RestController
@RequestMapping("pricetracker/api/v1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PriceTrackerController {

    private final EncryptionService encryptionService;
    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final CoinbaseClient coinbaseClient;

    @GetMapping
    private void getPrice(@RequestParam String id) {

        HttpEntity<String> entity = new HttpEntity<String>(getHeaders());
        ResponseEntity<String> response = restTemplate.exchange(String.format("https://api.coingecko.com/api/v3/coins/%s", id), HttpMethod.GET, entity, String.class);

        createCryptoCurrencyDTO(response.getBody());
    }

    @PostMapping
    private void createPortfolio() throws Exception {
        coinbaseClient.createPortfolio();
    }

    @PostMapping("/users")
    private void addUser(@RequestBody UserDTO user) {
        String secret = user.getCoinbaseAPIKey().getSecret();
        String encryptedSecret = secret == null ? null : encryptionService.encrypt(secret);

        String sql = "INSERT INTO USERS (username, password_hash, api_key_name, api_key_secret) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "username = VALUES(username), " +
                "password_hash = VALUES(password_hash), " +
                "api_key_secret = VALUES(api_key_secret), " +
                "api_key_name = VALUES(api_key_name)";

        Object[] batchArgs = new Object[]{
                user.getUsername(),
                user.getPassword(),
                user.getCoinbaseAPIKey().getName(),
                encryptedSecret
        };

        jdbcTemplate.update(sql, batchArgs);
    }

    public CryptoCurrencyDTO createCryptoCurrencyDTO(String body) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CryptoCurrencyDTO cryptoCurrency = objectMapper.readValue(body, CryptoCurrencyDTO.class);
            return cryptoCurrency;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
