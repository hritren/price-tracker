package price.tracker.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import price.tracker.coinbase.dto.CoinbaseAPIKeyDTO;

@Getter
@Data
@AllArgsConstructor
public class UserDTO {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("coinbaseAPIKey")
    private CoinbaseAPIKeyDTO coinbaseAPIKey;
}
