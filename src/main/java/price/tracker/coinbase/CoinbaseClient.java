package price.tracker.coinbase;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import price.tracker.bot.Strategy;
import price.tracker.coinbase.dto.OrderDTO;
import price.tracker.coinbase.dto.PortfolioDTO;
import price.tracker.coinbase.dto.PortfolioResponseDTO;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CoinbaseClient {

    private static final String ACCOUNTS_URI = "https://api.coinbase.com/api/v3/brokerage/accounts";
    private static final String ACCOUNTS_URL = "api.coinbase.com/api/v3/brokerage/accounts";
    private static final String ORDERS_URI = "https://api.coinbase.com/api/v3/brokerage/orders";
    private static final String ORDERS_URL = "api.coinbase.com/api/v3/brokerage/orders";
    private static final String PORTFOLIO_URI = "https://api.coinbase.com/api/v3/brokerage/portfolios";
    private static final String PORTFOLIO_URl = "api.coinbase.com/api/v3/brokerage/portfolios";
    private static final String LIST_ORDERS_URI = "https://api.coinbase.com/api/v3/brokerage/orders/historical/batch";
    private static final String LIST_ORDERS_URL = "api.coinbase.com/api/v3/brokerage/orders/historical/batch";

    private final CoinbaseJWTService coinbaseJWTService;
    private final RestTemplate restTemplate;

    public void botAction(String api_key_name, Strategy strategy) {
//        List<OrderDTO> orders = getOrders();
    }

    public void getOrders() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(getHeaders("GET", LIST_ORDERS_URL));
        ResponseEntity<String> response = restTemplate.exchange(LIST_ORDERS_URI, HttpMethod.GET, entity, String.class);

        String body = response.getBody();
        System.out.println(body);
    }

    public void placeOrder(String productId, String side) throws Exception {
        HttpEntity<OrderDTO> entity = new HttpEntity<>(createOrder(productId, side), getHeaders("POST", ORDERS_URL));
        ResponseEntity<String> response = restTemplate.exchange(ORDERS_URI, HttpMethod.POST, entity, String.class);

        String body = response.getBody();
        System.out.println(body);
    }

    private OrderDTO createOrder(String productId, String side) {
        return new OrderDTO("", "", "", null);
    }

    public void getAccounts() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(getHeaders("GET", ACCOUNTS_URL));
        ResponseEntity<String> response = restTemplate.exchange(ACCOUNTS_URI, HttpMethod.GET, entity, String.class);

        String body = response.getBody();
        System.out.println(body);
    }

    public void getPortfolios() {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(getHeaders("GET", PORTFOLIO_URl));
            ResponseEntity<String> response = restTemplate.exchange(PORTFOLIO_URI, HttpMethod.GET, entity, String.class);

            String body = response.getBody();
            System.out.println(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String createPortfolio() throws Exception {
        try {
            HttpEntity<PortfolioDTO> entity = new HttpEntity<>(new PortfolioDTO("TEST_NAME"), getHeaders("POST", PORTFOLIO_URl));
            ResponseEntity<String> response = restTemplate.exchange(PORTFOLIO_URI, HttpMethod.POST, entity, String.class);

            String body = response.getBody();
            System.out.println(body);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(body, PortfolioResponseDTO.class).getUuid();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deletePortfolio(String uuid) throws Exception {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(getHeaders("DELETE", PORTFOLIO_URl + "/" + uuid));
            ResponseEntity<String> response = restTemplate.exchange(PORTFOLIO_URI + "/" + uuid, HttpMethod.DELETE, entity, String.class);

            String body = response.getBody();
            System.out.println(body);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private HttpHeaders getHeaders(String requestMethod, String URL) throws Exception {
        String jwt = coinbaseJWTService.getJWT(requestMethod, URL);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);
        return headers;
    }


}
