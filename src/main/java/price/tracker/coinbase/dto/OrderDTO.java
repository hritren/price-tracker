package price.tracker.coinbase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class OrderDTO {

    private String client_order_id;
    private String product_id;
    private String side;
    private OrderConfigurationDTO orderConfiguration;
}
