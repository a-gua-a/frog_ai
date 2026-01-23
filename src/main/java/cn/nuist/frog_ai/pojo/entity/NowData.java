package cn.nuist.frog_ai.pojo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NowData {

    // 观测时间
    @JsonProperty("obsTime")
    private String obsTime;

    // 温度，单位：摄氏度
    @JsonProperty("temp")
    private String temp;

    // 体感温度，单位：摄氏度
    @JsonProperty("feelsLike")
    private String feelsLike;

    // 天气图标代码
    @JsonProperty("icon")
    private String icon;

    // 天气描述
    @JsonProperty("text")
    private String text;

    // 风向360角度
    @JsonProperty("wind360")
    private String wind360;

    // 风向
    @JsonProperty("windDir")
    private String windDir;

    // 风力等级
    @JsonProperty("windScale")
    private String windScale;

    // 风速，单位：公里/小时
    @JsonProperty("windSpeed")
    private String windSpeed;

    // 相对湿度，单位：%
    @JsonProperty("humidity")
    private String humidity;

    // 降水量，单位：毫米
    @JsonProperty("precip")
    private String precip;

    // 大气压强，单位：百帕
    @JsonProperty("pressure")
    private String pressure;

    // 能见度，单位：公里
    @JsonProperty("vis")
    private String vis;

    // 云量，单位：%
    @JsonProperty("cloud")
    private String cloud;

    //露点温度，单位：摄氏度
    @JsonProperty("dew")
    private String dew;
}
