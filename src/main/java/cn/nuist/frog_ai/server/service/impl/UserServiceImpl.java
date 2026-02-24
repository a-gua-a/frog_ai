package cn.nuist.frog_ai.server.service.impl;

import cn.nuist.frog_ai.pojo.entity.User;
import cn.nuist.frog_ai.server.mapper.UserMapper;
import cn.nuist.frog_ai.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.getByUsername(username);
        log.info("查询到用户:{}",user);
        if (user == null) {
            User newUser = User.builder()
                    .username(username)
                    .password(password)
                    .build();
            userMapper.insert(newUser);
            return newUser;
        }
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

}
