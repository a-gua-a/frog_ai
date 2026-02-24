package cn.nuist.frog_ai.server.mapper;

import cn.nuist.frog_ai.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username}")
    User getByUsername(String username);

    @Select("select * from user where id = #{id}")
    User getById(Integer id);

    void insert(User user);
}
