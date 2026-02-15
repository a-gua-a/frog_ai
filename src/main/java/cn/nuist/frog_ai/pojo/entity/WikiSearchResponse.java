package cn.nuist.frog_ai.pojo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WikiSearchResponse {

    private String batchcomplete;

    @JsonProperty("continue")
    private Continue continueObj;

    private Query query;
}
