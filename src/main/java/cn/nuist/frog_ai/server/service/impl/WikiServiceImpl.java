package cn.nuist.frog_ai.server.service.impl;

import cn.nuist.frog_ai.common.utils.JsonUtils;
import cn.nuist.frog_ai.pojo.entity.WikiSearchResponse;
import cn.nuist.frog_ai.server.service.WikiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class WikiServiceImpl implements WikiService {

    /**
     * 根据关键词搜索维基百科，获取相关页面大致信息
     */
    @Override
    public WikiSearchResponse search(String question) {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://wiki.biligame.com/zspms/api.php?action=query&format=json&list=search&srsearch=" + question;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            WikiSearchResponse wikiSearchResponse = JsonUtils.fromJson(response.body(), WikiSearchResponse.class);
            return wikiSearchResponse;
        } catch (Exception e) {
            log.error("关键词查询失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 根据页面ID获取页面内容
     */
    @Override
    public String getPageContent(Integer pageId) {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://wiki.biligame.com/zspms/api.php?action=query&format=json&pageids="
                + pageId + "&prop=revisions&rvprop=content&rvslots=main";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            String content = root.path("query").path("pages").path(pageId.toString()).path("revisions").path(0).path("slots").path("main").path("*").asText();
            log.info("页面内容查询成功");
            return content;
        } catch (Exception e) {
            log.error("页面内容查询失败：{}", e.getMessage());
            return null;
        }
    }

}
