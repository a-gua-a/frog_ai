package cn.nuist.frog_ai.server.service.impl;

import cn.nuist.frog_ai.pojo.dto.FileChatDTO;
import cn.nuist.frog_ai.server.service.AIChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AIChatServiceImpl implements AIChatService {

    @Autowired
    private ChatClient chatClient;

    @Override
    public String chat(String message, String conversationId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,conversationId))
                .call()
                .content();
    }

    @Override
    public String chatWithFile(FileChatDTO dto) {

        if(dto.getFileNames() == null || dto.getFileNames().isEmpty()){
            throw new IllegalArgumentException("文件异常");
        }
        String filenameList = "";
        for(String fileName : dto.getFileNames()){
            filenameList += fileName + "\n";
        }
        // 增加阅读文件提示
        String systemMessage = "请先阅读下列文件：" + filenameList;
        String userMessage = dto.getMessage();
        String conversationId = dto.getConversationId();
        return chatClient
                .prompt()
                .system(systemMessage)
                .user(userMessage)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,conversationId))
                .call()
                .content();
    }
}
