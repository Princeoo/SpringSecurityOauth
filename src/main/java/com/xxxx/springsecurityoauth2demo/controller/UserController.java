package com.xxxx.springsecurityoauth2demo.controller;


import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/user")
public class UserController {


    @RequestMapping("/getCurrentUser")
     public  Object getCurrentUser(Authentication authentication, HttpServletRequest request){

        String  head =request.getHeader("Authorization");
        String token =head.substring(head.indexOf("bearer") + 7);//拿到JWT令牌


        return  Jwts.parser()
                .setSigningKey("test_key".getBytes(StandardCharsets.UTF_8))//密钥名字
                .parseClaimsJws(token)//令牌解析
                .getBody()
                ;
    }



}
