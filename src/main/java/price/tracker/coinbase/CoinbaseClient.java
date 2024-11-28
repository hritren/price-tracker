package price.tracker.coinbase;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import price.tracker.coinbase.dto.PortfolioDTO;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CoinbaseClient {

    private static final String ACCOUNTS_URI = "https://api.coinbase.com/api/v3/brokerage/accounts";
    private static final String ACCOUNTS_URL = "api.coinbase.com/api/v3/brokerage/accounts";
    private static final String TEST_URI = "https://api.coinbase.com/api/v3/brokerage/products";
    private static final String TEST_URL = "api.coinbase.com/api/v3/brokerage/products";
    private static final String PORTFOLIO_URI = "https://api.coinbase.com/api/v3/brokerage/portfolios";
    private static final String PORTFOLIO_URl = "api.coinbase.com/api/v3/brokerage/portfolios";

    private final CoinbaseJWTService coinbaseJWTService;
    private final RestTemplate restTemplate;

    public void getAccounts() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(getHeaders("GET", ACCOUNTS_URL));
        ResponseEntity<String> response = restTemplate.exchange(ACCOUNTS_URI, HttpMethod.GET, entity, String.class);

        String body = response.getBody();
        System.out.println(body);
    }

    public void createPortfolio() throws Exception {
        try {
            HttpEntity<PortfolioDTO> entity = new HttpEntity<>(new PortfolioDTO("TEST_NAME"), getHeaders("POST", PORTFOLIO_URl));
            ResponseEntity<String> response = restTemplate.exchange(PORTFOLIO_URI, HttpMethod.POST, entity, String.class);

            String body = response.getBody();
            System.out.println(body);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testRequest() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(getHeaders("GET", PORTFOLIO_URl));
        ResponseEntity<String> response = restTemplate.exchange(PORTFOLIO_URI, HttpMethod.GET, entity, String.class);

        String body = response.getBody();
        System.out.println(body);
    }

    private HttpHeaders getHeaders(String requestMethod, String URL) throws Exception {
        String jwt = coinbaseJWTService.getJWT(requestMethod, URL);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);
        return headers;
    }
}
