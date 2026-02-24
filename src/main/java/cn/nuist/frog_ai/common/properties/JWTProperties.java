package cn.nuist.frog_ai.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "frog.jwt")
public class JWTProperties {

    private String userSecretKey;
    private long userTtl;
    private String userTokenName;
}
