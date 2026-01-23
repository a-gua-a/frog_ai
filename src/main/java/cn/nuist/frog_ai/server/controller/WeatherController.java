package cn.nuist.frog_ai.server.controller;

import cn.nuist.frog_ai.pojo.entity.WeatherResponse;
import cn.nuist.frog_ai.server.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/search")
    public String getWeather(@RequestParam("locationId") String locationId) {
         return weatherService.getWeather(locationId).getNow().getTemp();
    }

    @GetMapping("/locationId")
    public String getLocationId(@RequestParam("cityName") String cityName) {
        return weatherService.getLocationId(cityName).getLocation().get(0).getId();
    }
}
