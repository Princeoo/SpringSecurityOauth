package com.xxxx.springsecurityoauth2demo.sevice.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxxx.springsecurityoauth2demo.mapper.UserMapper;
import com.xxxx.springsecurityoauth2demo.pojo.User;
import com.xxxx.springsecurityoauth2demo.pojo.UserRole;
import com.xxxx.springsecurityoauth2demo.sevice.UserRoleService;
import com.xxxx.springsecurityoauth2demo.sevice.UserService;
import com.xxxx.springsecurityoauth2demo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class UserServiceImpl   extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService roleService;
    @Autowired
    private RedisUtil redisUtil;

    HttpSession session;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 通过用户名查询用户
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));

        // 放入session
        Map map = new HashMap();
        String userString = JSON.toJSONString(user);

        map.put(user.getUsername(),userString);
        redisUtil.hmset("user",map);

        //创建一个新的UserDetails对象，最后验证登陆的需要
        UserDetails userDetails=null;
        if(user!=null){
            //System.out.println("未加密："+user.getPassword());
            //String BCryptPassword = new BCryptPasswordEncoder().encode(user.getPassword());
            // 登录后会将登录密码进行加密，然后比对数据库中的密码，数据库密码需要加密存储！
            String password = user.getPassword();

            //创建一个集合来存放权限
            Collection<GrantedAuthority> authorities = getAuthorities(user);
            //实例化UserDetails对象
            //从上面UserDetailsService 可以知道最终交给Spring Security的是UserDetails 。该接口是提供用户信息的核心接口。该接口实现仅仅存储用户的信息。后续会将该接口提供的用户信息封装到认证对象Authentication中去。UserDetails 默认提供了：
            //用户的加密后的密码， 不加密会使用{noop}前缀
            //应用内唯一的用户名
            //账户是否过期
            //账户是否锁定
            //凭证是否过期
            //用户是否可用
            //用户的权限集， 默认需要添加ROLE_ 前缀
            userDetails=new org.springframework.security.core.userdetails.User
                    (username,
                            password,
                            true,
                            true,
                            true,
                            true, authorities);
//            userDetails=new org.springframework.security.core.userdetails.User
//                    (username,
//                            password,
//                            AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        }
        return userDetails;

    }

    // 获取角色信息，拿权限
    private Collection<GrantedAuthority> getAuthorities(User user){
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        UserRole role = roleService.getById(user.getRoleId());
        //注意：这里每个权限前面都要加ROLE_。否在最后验证不会通过
        authList.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
//        authList.add(new SimpleGrantedAuthority("admin"));
        return authList;
    }
}
