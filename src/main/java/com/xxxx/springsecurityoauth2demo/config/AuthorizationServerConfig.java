package com.xxxx.springsecurityoauth2demo.config;


import com.xxxx.springsecurityoauth2demo.sevice.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;


//授权服务器配置
//验证用户登陆的服务名和密码，验证成功返回tocken
//验证tocken
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceImpl userServiceimpl;

//     @Autowired
//     @Qualifier("redisTokenStore")
//     private TokenStore tokenStore;

     @Autowired
     @Qualifier("jwtTokenStore")
     private TokenStore tokenStore;

     @Autowired
     private JwtAccessTokenConverter jwtAccessTokenConverter;

     @Autowired
     private   JwtTokenEnhancer jwtTokenEnhancer;

    //使用密码模式所需配置
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //配置jwt内容增强器
        TokenEnhancerChain enhancerChain=new TokenEnhancerChain();
        List<TokenEnhancer> delegates=new ArrayList<>();
        delegates.add(jwtTokenEnhancer);
        delegates.add(jwtAccessTokenConverter);//把自定义声明转换
        enhancerChain.setTokenEnhancers(delegates);
        endpoints.authenticationManager(authenticationManager)
                 .userDetailsService(userServiceimpl)
                  //配置存储凌派策略
                //accessToken转成JWTtocken
                 .tokenStore(tokenStore)
                 .accessTokenConverter(jwtAccessTokenConverter)
                 .tokenEnhancer(enhancerChain)
                  ;
//                 .tokenStore(tokenStore); //存入redis

    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

      clients.inMemory()
              //配置client-id
              .withClient("admin")
               //配置client-secret 密码
              .secret(passwordEncoder.encode("112233"))
               //配置访问token的有效期
              .accessTokenValiditySeconds(3600)
              .refreshTokenValiditySeconds(864000)
               //配置redirect——uri 用于授权成功后跳转
              .redirectUris("http://localhost:8081/login")
//              .autoApprove(true)//自动授权
              //配置申请的权限范围
              .scopes("admin")
              //配置grant_type表示授权类型
              .authorizedGrantTypes("password","refresh_token","authorization_code")

      ;


    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //获取密钥必须身份认证，单点登陆必备
         security.tokenKeyAccess("isAuthenticated()");
    }
}
