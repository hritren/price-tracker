package price.tracker.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootConfiguration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplateBean() {
        return new RestTemplate();
    }
}
