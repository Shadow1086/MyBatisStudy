package com.mybatis.mapper;

import com.mybatis.pojo.User;

import java.util.List;

/**
 * 类名: UserMapper
 * 创建时间: 2026/1/21 23:41
 * 项目描述:
 *
 * @author htLiang
 */
public interface UserMapper {
    List<User> selectAll();
}
