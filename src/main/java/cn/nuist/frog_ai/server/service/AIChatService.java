package cn.nuist.frog_ai.server.service;

import cn.nuist.frog_ai.pojo.dto.FileChatDTO;

public interface AIChatService {

    String chat(String message, String conversationId);

    String chatWithFile(FileChatDTO dto);
}
