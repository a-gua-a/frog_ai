package cn.nuist.frog_ai.server.tools;

import cn.nuist.frog_ai.pojo.entity.SearchResult;
import cn.nuist.frog_ai.pojo.entity.WikiSearchResponse;
import cn.nuist.frog_ai.server.service.WikiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class WikiTools {

    @Autowired
    private WikiService wikiService;

    @Tool(description = "根据关键词搜索维基百科，获取相关页面ID,关键词中不允许出现空格等未编码的特殊字符")
    public List<Integer> getPageIds(@ToolParam(description = "关键词") String question) {
        log.info("根据关键词搜索维基百科，获取相关页面ID，关键词：{}", question);
        WikiSearchResponse wikiSearchResponse = wikiService.search(question);
        if (wikiSearchResponse != null
                && wikiSearchResponse.getQuery() != null
                && wikiSearchResponse.getQuery().getSearch() != null) {
            List<Integer> pageIds = new ArrayList<>();
            for (SearchResult searchResult : wikiSearchResponse.getQuery().getSearch()) {
                pageIds.add(searchResult.getPageid());
            }
            log.info("根据关键词搜索维基百科，获取相关页面ID成功，页面ID列表：{}", pageIds);
            return pageIds;
        }else{
            return null;
        }
    }

    @Tool(description = "根据页面ID获取维基百科页面内容")
    public String getPageContent(@ToolParam(description = "单个页面ID") Integer pageId) {
        log.info("根据页面ID获取维基百科页面内容，页面ID：{}", pageId);
        String pageContent = wikiService.getPageContent(pageId);
        if (pageContent != null) {
            log.info("根据页面ID获取维基百科页面内容成功，页面ID：{}", pageId);
            return pageContent;
        }else{
            return null;
        }
    }
}
