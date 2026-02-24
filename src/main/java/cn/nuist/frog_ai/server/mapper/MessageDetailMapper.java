package cn.nuist.frog_ai.server.mapper;

import cn.nuist.frog_ai.pojo.entity.MessageDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageDetailMapper {

    void insert(MessageDetail messageDetail);

    MessageDetail getDetail(String conversationId, Integer messageId);

}
