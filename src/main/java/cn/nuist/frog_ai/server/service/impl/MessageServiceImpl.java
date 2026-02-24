package cn.nuist.frog_ai.server.service.impl;

import cn.nuist.frog_ai.common.context.BaseContext;
import cn.nuist.frog_ai.pojo.entity.Message;
import cn.nuist.frog_ai.pojo.entity.MessageDetail;
import cn.nuist.frog_ai.pojo.entity.Sentence;
import cn.nuist.frog_ai.pojo.vo.ConversationDetailVO;
import cn.nuist.frog_ai.server.mapper.MemoryMapper;
import cn.nuist.frog_ai.server.mapper.MessageDetailMapper;
import cn.nuist.frog_ai.server.mapper.MessageMapper;
import cn.nuist.frog_ai.server.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MessageDetailMapper messageDetailMapper;
    @Autowired
    private MemoryMapper memoryMapper;

    @Override
    public List<Message> listByUserId(Integer userId) {
        return messageMapper.selectByUserId(userId);
    }

    @Override
    public void addMessage(String conversationId,String description) {
        Integer userId = BaseContext.getCurrentId();
        Message message = Message.builder()
                .userId(userId)
                .description(description)
                .build();
        messageMapper.insert(message);

        MessageDetail messageDetail = MessageDetail.builder()
                .messageId(message.getId())
                .content(conversationId)
                .build();
        messageDetailMapper.insert(messageDetail);
    }

    @Override
    public ConversationDetailVO getDetail(String conversationId, Integer messageId) {
        MessageDetail messageDetail = messageDetailMapper.getDetail(conversationId, messageId);
        if (messageDetail == null) {
            throw new IllegalArgumentException("消息不存在");
        }
        List<Map<String, String>> list = memoryMapper.getByConversationId(conversationId);
        List<Sentence> sentences = new ArrayList<>();
        for (Map<String, String> map : list) {
            Sentence sentence = new Sentence();
            if(map.get("type").equals("USER")) {
                sentence.setRoleAsUser();
            } else if(map.get("type").equals("ASSISTANT")) {
                sentence.setRoleAsBot();
            }
            sentence.setText(map.get("content"));
            log.info("role: {}, text: {}", sentence.getRole(), sentence.getText());
            sentences.add(sentence);
        }
        ConversationDetailVO conversationDetailVO = ConversationDetailVO.builder()
                .sentences(sentences)
                .build();
        return conversationDetailVO;
    }

    @Override
    public void updateDescription(Integer id, String description) {
        Message message = messageMapper.getById(id);
        if (message == null) {
            throw new IllegalArgumentException("消息不存在");
        }
        message.setDescription(description);
        messageMapper.updateById(message);
    }
}
