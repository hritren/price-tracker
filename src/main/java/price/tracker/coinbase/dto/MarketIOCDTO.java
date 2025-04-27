package price.tracker.coinbase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class MarketIOCDTO {
    private String quote_size;
    private String base_size;
}
