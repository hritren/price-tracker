package price.tracker.coinbase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class OrderConfigurationDTO {

    private MarketIOCDTO marketIOC;
}
