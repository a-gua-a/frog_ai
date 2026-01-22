package cn.nuist.frog_ai.server.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private VectorStore vectorStore;

    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    @PostMapping("/loadDocument")
    public void loadDocument(){
        Resource resource = new FileSystemResource("src/main/resources/document/test.txt");
        TextReader reader = new TextReader(resource);
        List<Document> documentList = reader.read();
        vectorStore.add(documentList);
    }
}
