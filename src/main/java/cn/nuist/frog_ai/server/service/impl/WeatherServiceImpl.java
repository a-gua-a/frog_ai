package cn.nuist.frog_ai.server.service.impl;

import cn.nuist.frog_ai.common.properties.WeatherProperties;
import cn.nuist.frog_ai.common.utils.GzipDecompressor;
import cn.nuist.frog_ai.common.utils.JsonUtils;
import cn.nuist.frog_ai.pojo.entity.LocationResponse;
import cn.nuist.frog_ai.pojo.entity.WeatherResponse;
import cn.nuist.frog_ai.server.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    @Autowired
    private WeatherProperties weatherProperties;

    /**
     * 根据城市LocationID查询实时天气状况
     */
    @Override
    public WeatherResponse getWeather(String locationId) {
        HttpClient client = HttpClient.newHttpClient();
        String apiUrl = weatherProperties.getApiHost() + "/v7/weather/now?location=" + locationId;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("X-QW-Api-Key", weatherProperties.getApiKey())
                    .GET()
                    .build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            byte[] compressedBody = response.body();
            // 解压缩响应体
            String decompressedBody = GzipDecompressor.decompressToString(compressedBody);
            log.info("查询天气成功, locationId: {}", locationId);
            WeatherResponse weatherResponse = JsonUtils.fromJson(decompressedBody, WeatherResponse.class);
            return weatherResponse;
        } catch (Exception e) {
            log.error("查询天气失败, locationId: {}", locationId, e);
            return null;
        }
    }

    /**
     * 根据城市名称查询城市LocationID
     */
    @Override
    public LocationResponse getLocationId(String cityName) {
        HttpClient client = HttpClient.newHttpClient();
        String apiUrl = weatherProperties.getApiHost() + "/geo/v2/city/lookup?location=" + cityName;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("X-QW-Api-Key", weatherProperties.getApiKey())
                    .GET()
                    .build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            byte[] compressedBody = response.body();
            // 解压缩响应体
            String decompressedBody = GzipDecompressor.decompressToString(compressedBody);
            log.info("查询城市LocationID成功, cityName: {}", cityName);
            LocationResponse locationResponse = JsonUtils.fromJson(decompressedBody, LocationResponse.class);
            return locationResponse;
        } catch (Exception e) {
            log.error("查询城市LocationID失败, cityName: {}", cityName, e);
            return null;
        }
    }
}
