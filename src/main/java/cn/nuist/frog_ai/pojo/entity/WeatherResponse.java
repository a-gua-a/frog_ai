package cn.nuist.frog_ai.pojo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeatherResponse {
    @JsonProperty("code")
    private String code;

    @JsonProperty("updateTime")
    private String updateTime;

    @JsonProperty("fxLink")
    private String fxLink;

    @JsonProperty("now")
    private NowData now;

    @JsonProperty("refer")
    private Refer refer;
}
