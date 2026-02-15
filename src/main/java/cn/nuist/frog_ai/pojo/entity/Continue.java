package cn.nuist.frog_ai.pojo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Continue {

    private int sroffset;

    @JsonProperty("continue")
    private String continueStr;

}
