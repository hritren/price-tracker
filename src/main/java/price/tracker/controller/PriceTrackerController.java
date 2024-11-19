package price.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import price.tracker.coingecko.dto.CryptoCurrencyDTO;

@RestController
@RequestMapping("pricetracker/api/v1")
public class PriceTrackerController {

    private RestTemplate restTemplate;

    @Autowired
    public PriceTrackerController(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @GetMapping
    private void getPrice(@RequestParam String id) {

        HttpEntity<String> entity = new HttpEntity<String>(getHeaders());
        ResponseEntity<String> response = restTemplate.exchange(String.format("https://api.coingecko.com/api/v3/coins/%s", id), HttpMethod.GET, entity, String.class);

        createCryptoCurrencyDTO(response.getBody());
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
