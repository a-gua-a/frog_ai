package cn.nuist.frog_ai.server.controller;

import cn.nuist.frog_ai.server.service.WikiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wiki")
public class WikiController {

    @Autowired
    private WikiService wikiService;

    @GetMapping("/search")
    public String search(@RequestParam("question") String question) {
        return wikiService.search(question).toString();
    }

    @GetMapping("/pageContent")
    public String pageContent(@RequestParam("pageId") Integer pageId) {
        return wikiService.getPageContent(pageId);
    }
}
