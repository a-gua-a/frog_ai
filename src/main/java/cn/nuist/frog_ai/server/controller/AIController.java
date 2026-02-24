package cn.nuist.frog_ai.server.controller;

import cn.nuist.frog_ai.common.context.BaseContext;
import cn.nuist.frog_ai.pojo.entity.Message;
import cn.nuist.frog_ai.pojo.vo.ConversationDetailVO;
import cn.nuist.frog_ai.server.service.AIAudioSpeechService;
import cn.nuist.frog_ai.server.service.AIChatService;
import cn.nuist.frog_ai.server.service.MessageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private AIAudioSpeechService aiAudioSpeechService;
    @Autowired
    private AIChatService aiChatService;
    @Autowired
    private MessageService messageService;

    @CrossOrigin
    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String conversationId = request.get("conversationId");
        return aiChatService.chat(message, conversationId);
    }


    /**
     * 返回文本、流式返回语音
     */
    @CrossOrigin(exposedHeaders = "responseText")
    @PostMapping("/audio")
    public StreamingResponseBody text2Audio(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String message = request.get("message");
        String conversationId = request.get("conversationId");
        String text = aiChatService.chat(message, conversationId);
        String encodedText = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
        response.setHeader("responseText", encodedText);
        String audioUrl = null;
        try {
            audioUrl = aiAudioSpeechService.text2Audio(text);
        } catch (Exception e) {
            log.error("文本转语音失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        String finalAudioUrl = audioUrl;
        return outputStream -> {
            if(finalAudioUrl == null){
                log.error("音频url为空");
                return;
            }
            try (InputStream audioInputStream = new URL(finalAudioUrl).openStream()) {
                byte[] buffer = new byte[4096]; // 4KB缓冲区
                int bytesRead;
                while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush(); // 立即刷新，确保数据及时发送到前端
                }
                log.info("音频流传输完成");
            } catch (Exception e) {
                log.error("传输音频流时出错", e);
            }
        };
    }

    /**
     * 查询历史聊天记录
     */
    @CrossOrigin
    @GetMapping("/history")
    public List<Message> getMessageList(){
        log.info("查询用户{}的聊天记录", BaseContext.getCurrentId());
        Integer userId = BaseContext.getCurrentId();
        List<Message> messageList = messageService.listByUserId(userId);
        log.info("用户{}的聊天记录:{}", userId, messageList);
        return messageList;
    }

    /**
     * 查询历史聊天细节
     */
    @CrossOrigin
    @PostMapping("/detail")
    public ConversationDetailVO detail(@RequestBody Map<String, String> request) {
        String conversationId = request.get("conversationId");
        Integer id = Integer.valueOf(request.get("id"));
        try {
            return messageService.getDetail(conversationId, id);
        } catch (Exception e) {
            log.error("查询聊天细节失败", e);
            return null;
        }
    }

    /**
    * 新增聊天
    */
    @CrossOrigin
    @PostMapping("/addChat")
    public boolean addChat(@RequestBody Map<String, String> request){
        String conversationId = request.get("conversationId");
        String description = request.get("description");
        try {
            messageService.addMessage(conversationId,description);
        } catch (Exception e) {
            log.error("添加聊天失败", e);
            return false;
        }
        return true;
    }

    /**
     * 更改聊天记录备注
     */
    @CrossOrigin
    @PutMapping("/updateChat")
    public boolean updateChat(@RequestBody Map<String, String> request){
        log.info("更新聊天记录备注,id={},description={}",request.get("id"),request.get("description"));
        Integer id = Integer.valueOf(request.get("id"));
        String description = request.get("description");
        try {
            messageService.updateDescription(id, description);
        } catch (Exception e) {
            log.error("更新聊天失败", e);
            return false;
        }
        return true;
    }

    @PostMapping("/loadDocument")
    public void loadDocument(){
        Resource resource = new FileSystemResource("src/main/resources/document/序章.txt");
        TextReader reader = new TextReader(resource);
        List<Document> documentList = reader.read();
        vectorStore.add(documentList);
    }

    //测试创建语音
    @PostMapping("/createVoice")
    public void createVoice(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        try {
            aiAudioSpeechService.call(text);
        } catch (Exception e) {
            log.error("创建语音失败", e);
        }
    }
}
