package com.xxxx.springsecurityoauth2demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxxx.springsecurityoauth2demo.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author princeoo
 * @since 2021-01-13
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
