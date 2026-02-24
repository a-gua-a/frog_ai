package cn.nuist.frog_ai.server.service.impl;

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
}
