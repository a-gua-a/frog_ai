package cn.nuist.frog_ai.server.mapper;

import cn.nuist.frog_ai.pojo.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Select("select * from message where user_id = #{userId}")
    List<Message> selectByUserId(Integer userId);

    @Select("select * from message where id = #{id}")
    Message getById(Integer id);

    void insert(Message message);

    void updateById(Message message);
}
