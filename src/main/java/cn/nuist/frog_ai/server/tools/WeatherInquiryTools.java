package cn.nuist.frog_ai.server.tools;

import cn.nuist.frog_ai.server.service.WeatherService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WeatherInquiryTools {

    @Autowired
    private WeatherService weatherService;

    @Tool(description = "根据城市名称查询城市LocationID")
    public String getLocationId(@ToolParam(description = "城市名称") String cityName){
        return weatherService.getLocationId(cityName).getLocation().get(0).getId();
    }

    @Tool(description = "根据城市LocationID查询实时温度")
    public String getWeather(@ToolParam(description = "城市LocationID") String locationId){
        return weatherService.getWeather(locationId).getNow().getTemp();
    }
}
