package cn.nuist.frog_ai.server.controller;

import cn.nuist.frog_ai.common.constant.JWTConstant;
import cn.nuist.frog_ai.common.properties.JWTProperties;
import cn.nuist.frog_ai.common.utils.JWTUtils;
import cn.nuist.frog_ai.pojo.entity.User;
import cn.nuist.frog_ai.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@Slf4j
public class LoginController {

    @Autowired
    UserService userService;
    @Autowired
    JWTProperties jwtProperties;

    @CrossOrigin
    @GetMapping("/login")
    public String login(String username, String password) {
        User user = null;
        log.info("用户名:{}",username);
        try{
            user = userService.login(username, password);
        }catch (Exception e){
            log.error("登录失败",e);
        }
        if(user == null) {
            return JWTConstant.FAILED;
        }
        HashMap<String,Object> claims = new HashMap<>();
        claims.put(JWTConstant.USER_ID,user.getId());
        String token = JWTUtils.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        log.info("token:{}",token);
        return token;
    }
}
