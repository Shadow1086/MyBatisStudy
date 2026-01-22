package com.mybatis;

import com.mybatis.mapper.UserMapper;
import com.mybatis.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 类名: MyBatisDemo
 * 创建时间: 2026/1/21 23:05
 * 项目描述:
 *  mybatis快速入门
 * @author htLiang
 */
public class MyBatisDemo {
    public static void main(String[] args)  throws IOException {
//        //1. 加载mybatis的核心配置文件，获取SqlSessionFactory对象(不需要记，只需写一遍)
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        //2. 获取sqlSession对象，用它来执行sql语句
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//
//        //方式一：3.执行sql语句
//        //List<User> users = sqlSession.selectList("test.selectAll");
//        //方式二：Mapper代理：
//        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//        List<User> users = userMapper.selectAll();
//        System.out.println(users);
//        //4.释放资源
//        sqlSession.close();

        SqlSessionFactory sqlSessionFactory;
        // 初始化块在字段声明之后执行
        {
            try {
                String resource = "mybatis-config.xml";
                InputStream inputStream = Resources.getResourceAsStream(resource);
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        String username = "zhangsan";
        String password = "123";
        User user = userMapper.select(username,password);
        System.out.println(user);
        sqlSession.close();
    }
}
