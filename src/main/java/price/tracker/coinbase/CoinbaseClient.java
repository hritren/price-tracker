package price.tracker.coinbase;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CoinbaseClient {

    private static final String URI = "https://api.coinbase.com/api/v3/brokerage/accounts";
    private static final String URL = "api.coinbase.com/api/v3/brokerage/accounts";

    private final CoinbaseJWTService coinbaseJWTService;
    private final RestTemplate restTemplate;

    public void getAccounts() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<String> response = restTemplate.exchange(URI, HttpMethod.GET, entity, String.class);

        String body = response.getBody();
        System.out.println(body);
    }

    private HttpHeaders getHeaders() throws Exception {
        String jwt = coinbaseJWTService.getJWT("GET", URL);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);
        return headers;
    }
}
