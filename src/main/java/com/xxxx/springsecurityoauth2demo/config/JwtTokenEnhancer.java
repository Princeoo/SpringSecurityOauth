package com.xxxx.springsecurityoauth2demo.config;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class JwtTokenEnhancer implements TokenEnhancer {



    //jwt存储自定义声明
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {

      Map<String,Object> info =new HashMap<>();

      info.put("enhance","enhance info");//自定义声明
        (  (DefaultOAuth2AccessToken)oAuth2AccessToken).setAdditionalInformation(info);


     return  oAuth2AccessToken;
    }
}
