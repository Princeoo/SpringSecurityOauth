package com.xxxx.springsecurityoauth2demo.config;

import com.xxxx.springsecurityoauth2demo.sevice.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserServiceImpl userServiceimpl;

    @Bean
    public PasswordEncoder passwordEncoder(){

        return  new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {

        return  super.authenticationManagerBean();
    }

//    // 用户授权验证
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userServiceimpl).passwordEncoder(passwordEncoder());
//    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // .denyAll();    //拒绝访问
        // .authenticated();    //需认证通过
        // .permitAll();    //无条件允许访问
        // 授权访问
        http.authorizeRequests()
//                .antMatchers("xxx.html").hasRole("user") //基于角色访问，角色是ROLE_user
                .antMatchers("/","/index").permitAll()
                .antMatchers("/register","/login","/toLogin").permitAll()//登陆不拦截
                .antMatchers("/*").permitAll()//登陆不拦截
                .antMatchers("/*").authenticated();


        // 登录配置
        http.formLogin()
                .usernameParameter("username")//前端表单名字
                .passwordParameter("password")
//                .loginPage("/toLogin")
                //必须和表单提交的接口一致，会去执行自定义逻辑 //单点登陆视频是login
                .loginProcessingUrl("/login")// 自定义登陆表单提交请求
                .defaultSuccessUrl("/index"); // 设置默认登录成功后跳转的页面


        // 注销配置
        http.headers().contentTypeOptions().disable();
        http.headers().frameOptions().disable(); // 图片跨域

//        http.csrf().disable();//关闭csrf功能:跨站请求伪造,默认只能通过post方式提交logout请求
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/oauth/**","/login/**","/logout/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .permitAll();
        http.logout().logoutSuccessUrl("/");

        // 记住我配置
        http.rememberMe().rememberMeParameter("remember");
    }


}
