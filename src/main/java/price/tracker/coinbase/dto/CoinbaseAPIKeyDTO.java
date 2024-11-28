package price.tracker.coinbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class CoinbaseAPIKeyDTO {


    @JsonProperty("name")
    private String name;

    @JsonProperty("secret")
    private String secret;
}
