package cn.nuist.frog_ai.server.service;

import cn.nuist.frog_ai.pojo.entity.Message;
import cn.nuist.frog_ai.pojo.vo.ConversationDetailVO;

import java.util.List;

public interface MessageService {

    List<Message> listByUserId(Integer userId);

    void addMessage(String conversationId,String description);

    ConversationDetailVO getDetail(String conversationId, Integer messageId);

    void updateDescription(Integer id, String description);
}
