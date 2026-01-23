package cn.nuist.frog_ai.server.service;

import cn.nuist.frog_ai.pojo.entity.LocationResponse;
import cn.nuist.frog_ai.pojo.entity.WeatherResponse;

public interface WeatherService {

     WeatherResponse getWeather(String locationId);

     LocationResponse getLocationId(String cityName);
}
