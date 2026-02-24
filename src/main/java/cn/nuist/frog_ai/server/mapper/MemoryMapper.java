package cn.nuist.frog_ai.server.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemoryMapper {

    List<Map<String,String>> getByConversationId(String conversationId);

}
