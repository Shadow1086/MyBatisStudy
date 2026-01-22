package com.mybatisImpl.curdImpl.test;

import com.mybatisImpl.curdImpl.mapper.BrandMapper;
import com.mybatisImpl.curdImpl.pojo.Brand;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名: MyBatisTest
 * 创建时间: 2026/1/22 11:25
 * 项目描述:
 *
 * @author htLiang
 */
public class MyBatisTest {
    //1. 加载mybatis的核心配置文件，获取SqlSessionFactory对象(不需要记，只需写一遍)
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

    @Test
    public void testSelectAll() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        BrandMapper brandMapper = sqlSession.getMapper(BrandMapper.class);
        List<Brand> brands = brandMapper.selectAll();
        System.out.println(brands);
        sqlSession.close();
    }

    @Test
    public void testSelectById() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        BrandMapper brandMapper = sqlSession.getMapper(BrandMapper.class);
        Brand brand = brandMapper.selectById(2);
        System.out.println(brand);
        sqlSession.close();
    }

    @Test
    public void testSelectByCondition() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        BrandMapper brandMapper = sqlSession.getMapper(BrandMapper.class);

        int status = 1;
        String companyName = "华为";
        String brandName = "华为";
        //处理参数
        companyName = "%" + companyName + "%";
        brandName = "%" + brandName + "%";


        // 散装参数：
        List<Brand> brands = brandMapper.selectByCondition(status, companyName, brandName);

        //对象参数,没在brandMapper.xml中声明，故不做演示
        Brand brand = new Brand();
        brand.setBrandName(brandName);
        brand.setCompanyName(companyName);
        brand.setStatus(status);
//        List<Brand> brands = brandMapper.selectByCondition(brand);

        //集合参数：
        Map map = new HashMap();
        map.put("status",status);
        map.put("conpany_name",companyName);
        map.put("brand_name",brandName);
        System.out.println(brands);
        sqlSession.close();
    }
    @Test
    public void testSelectConditionSingle(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        BrandMapper brandMapper = sqlSession.getMapper(BrandMapper.class);

//        int status = 1;
        String companyName = "华为";
//        String brandName = "华为";
        //处理参数
        companyName = "%" + companyName + "%";
//        brandName = "%" + brandName + "%";
        Brand brand = new Brand();
        brand.setCompanyName(companyName);
        List<Brand> brands = brandMapper.selectByConditionSingle(brand);
        System.out.println(brands);
    }
}
