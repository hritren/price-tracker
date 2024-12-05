package price.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import price.tracker.authentiacation.JWTService;
import price.tracker.coinbase.CoinbaseClient;
import price.tracker.coinbase.dto.CoinbaseAPIKeyDTO;
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
    private final JWTService jwtService;

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

    @PostMapping("/users/register")
    private ResponseEntity<String> registerUser(@RequestBody UserDTO user) {
        if (userExists(user)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A user with this username already exists.");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());

        String secret = user.getCoinbaseAPIKey().getSecret();
        String encryptedSecret = secret == null ? null : encryptionService.encrypt(secret);

        String sql = "INSERT INTO USERS (username, email, password_hash, api_key_name, api_key_secret) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "username = VALUES(username), " +
                "email = VALUES(email), " +
                "password_hash = VALUES(password_hash), " +
                "api_key_secret = VALUES(api_key_secret), " +
                "api_key_name = VALUES(api_key_name)";

        Object[] batchArgs = new Object[]{
                user.getUsername(),
                user.getEmail(),
                hashedPassword,
                user.getCoinbaseAPIKey().getName(),
                encryptedSecret
        };

        jdbcTemplate.update(sql, batchArgs);

        int id = getIDFromDB(user);
        String jwt = jwtService.issueJWT(id, user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jwt);
    }

    @PostMapping("/users/login")
    private ResponseEntity<String> login(@RequestBody UserDTO user) {
        if (!userExists(user)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Wrong username or password.");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String hashedPasswordDB = getPasswordFromDB(user);
        if (!passwordEncoder.matches(user.getPassword(), hashedPasswordDB)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Wrong username or password.");
        }

        int id = getIDFromDB(user);
        String jwt = jwtService.issueJWT(id, user.getUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .body(jwt);
    }

    @PostMapping("/users/key")
    private ResponseEntity<String> addKey(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CoinbaseAPIKeyDTO apiKey) {

        try {
            String token = authorizationHeader.replace("Bearer ", "");
            Claims claims = jwtService.validateJWT(token);

            String userId = claims.getSubject();
            String username = claims.get("username", String.class);

            if (apiKey.getName() == null || apiKey.getSecret() == null) {
                return ResponseEntity.badRequest().body("API key name and secret are required.");
            }

            String encryptedSecret = encryptionService.encrypt(apiKey.getSecret());

            String sql = "UPDATE users SET api_key_name = ?, api_key_secret = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

            int rowsUpdated = jdbcTemplate.update(sql, apiKey.getName(), encryptedSecret, userId);

            if (rowsUpdated == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            return ResponseEntity.ok("API key added successfully for user: " + username);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }
    }

    private boolean userExists(UserDTO user) {
        String username = user.getUsername();

        String sql = "SELECT COUNT(*) " +
                "FROM USERS " +
                "WHERE username = ?";

        Integer userCount = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                username);
        return userCount != null && userCount > 0;
    }

    private String getPasswordFromDB(UserDTO user) {
        String username = user.getUsername();

        String sql = "SELECT password_hash " +
                "FROM USERS " +
                "WHERE username = ?";

        return jdbcTemplate.queryForObject(
                sql,
                String.class,
                username);
    }

    private Integer getIDFromDB(UserDTO user) {
        String username = user.getUsername();

        String sql = "SELECT id " +
                "FROM USERS " +
                "WHERE username = ?";

        return jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                username);
    }

    private CryptoCurrencyDTO createCryptoCurrencyDTO(String body) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(body, CryptoCurrencyDTO.class);
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
