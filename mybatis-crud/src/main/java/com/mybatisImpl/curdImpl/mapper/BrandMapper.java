package com.mybatisImpl.curdImpl.mapper;

import com.mybatisImpl.curdImpl.pojo.Brand;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 类名: brandMapper
 * 创建时间: 2026/1/22 10:45
 * 项目描述:
 *
 * @author htLiang
 */
public interface BrandMapper {
    List<Brand> selectAll();

    Brand selectById(int id);
//多条件查询的三个方法
    List<Brand> selectByCondition(@Param("status") int status,
                                  @Param("companyName") String companyName,
                                  @Param("brandName") String brandName);
    List<Brand> selectByCondition(Brand brand);
    List<Brand> selectByCondition(Map map);

//    单条件查询
    List<Brand> selectByConditionSingle(Brand brand);

// 添加
    void add(Brand brand);
}
