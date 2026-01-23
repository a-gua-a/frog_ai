package cn.nuist.frog_ai.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
@Data
@ConfigurationProperties(prefix = "frog.weather")
public class WeatherProperties {

    private String apiKey;

    private String apiHost;
}
