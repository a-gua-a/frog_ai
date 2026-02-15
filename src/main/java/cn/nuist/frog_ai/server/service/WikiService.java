package cn.nuist.frog_ai.server.service;

import cn.nuist.frog_ai.pojo.entity.WikiSearchResponse;

public interface WikiService {

    WikiSearchResponse search(String question);

    String getPageContent(Integer pageId);
}
