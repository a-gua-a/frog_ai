package cn.nuist.frog_ai.pojo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocationResponse {

    @JsonProperty("code")
    private String code;

    @JsonProperty("location")
    private List<Location> location;

    @JsonProperty("refer")
    private Refer refer;
}
