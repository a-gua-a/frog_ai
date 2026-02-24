package cn.nuist.frog_ai.server.service;

import cn.nuist.frog_ai.pojo.entity.User;

public interface UserService {

    User login(String username, String password);
}
