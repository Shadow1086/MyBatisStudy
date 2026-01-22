# MyBatis学习

## 1. 配置相关信息

1. maven导入依赖

```xml

<dependencies>
    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.5.19</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.23</version>
    </dependency>
    <!--其他依赖的导入-->
</dependencies>
```

2. 创建`mybatis-config.xml`文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <!-- 数据库连接信息 -->
                <!-- 驱动 -->
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <!-- 数据库地址 -->
                <property name="url" value="${url}"/>
                <!-- 密码 -->
                <property name="password" value="${password}"/>
                <!-- 用户名 -->
                <property name="username" value="${username}"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <!--SQL语句映射文件-->
        <!--       没有使用Mapper代理开发-->
        <!--        <mapper resource="org/mybatis/example/BlogMapper.xml"/>-->
        <!--       使用Mapper代理开发：-->
        <package name="com.mybatis.mapper"/>
    </mappers>
</configuration>
```

3. 创建`xxxMapper.xml`配置文件
   **说明：** 此文件名中的xxx是根据所要操作的数据库起的名

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 写一个根标签：namespace 指定当前mapper的命名空间,相当于C中的using namespace std一样 -->
<!--当没有使用Mapper代理开发时，namespace可以随便设置为test ,...-->
<!--但是使用Mapper代理开发时，就需要写全，如下-->
<mapper namespace="com.mybatis.mapper.UserMapper">
    <!-- 根据所要操作的表 改写命名空间和编写SQL语句 -->
    <!--    如下的select 其中的id 就是要被引用的标识，相当于preparedStatement-->
    <select id="selectAll" resultType="com.mybatis.pojo.User">
        select * from tb_user;
    </select>
    <!-- 写增删改查的SQL语句 -->
</mapper>
```

4. 拥有一个实体类之后，就在main方法中写入以下：

```java
//1. 加载mybatis的核心配置文件，获取SqlSessionFactory对象(不需要记，只需写一遍,直接复制即可)
String resource = "mybatis-config.xml";
InputStream inputStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

//2. 获取sqlSession对象，用它来执行sql语句
SqlSession sqlSession = sqlSessionFactory.openSession();

//3.执行sql语句，"命名空间.方法id"
List<User> users = sqlSession.selectList("test.selectAll");
//3,使用Mapper代理开发：
UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
List<User> users = userMapper.selectAll();

System.out.

println(users);
//4. 释放资源
sqlSession.

close();
```

## 2. Mapper代理开发

**目的：**

- 解决原生方式中的硬编码

- 简化后期执行sql语句

**步骤：**

1. 定义与SQL映射文件同名的Mapper接口，并且将Mapper接口和SQL映射文件放置在统一目录下
    - 通过在resources中创建同样的包结构，比如resources/com/mybatis/mapper
2. 设置SQL映射文件的namespace属性为Mapper接口全限定名
    - 设置命名空间，如：com.mybatis.mapper.UserMapper
3. 在Mapper接口中定义方法，方法名就是SQL映射文件中SQL语句的id,并保持参数类型和返回值类型一致
4. 编码
    1. 通过SqlSession的getMapper方法获取Mapper接口的代理对象
    2. 调用对应方法完成sql的执行

```java
UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
List<User> users = userMapper.selectAll();
```

**说明：**
当按照以上规则使用代理开发时，在mybatis-config.xml中的SQL映射文件部分就可以使用包扫描的方式进行

```xml

<mapper resource="com/mybatis/mapper/UserMapper.xml"/>
<package name="com.mybatis.mapper"/>
```

## 3. 环境配置

mybatis-config.xml中:

1. environments:配置数据库连接环境信息，可以有多个environment，通过default属性切换不同的environment,如下：

```xml 
<!--  默认数据源，可按需更改-->
<environments default="development">
    <!--   开发的数据源-->
    <environment id="development">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <property name="driver" value="com.mysql.jdbc.Driver"/>
            <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=false"/>
            <property name="password" value="password"/>
            <property name="username" value="root"/>
        </dataSource>
    </environment>
    <!--   测试的数据源-->
    <environment id="test">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <property name="driver" value="com.mysql.jdbc.Driver"/>
            <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=false"/>
            <property name="password" value="password"/>
            <property name="username" value="root"/>
        </dataSource>
    </environment>
</environments>
```

2. transactionManager:事务管理的驱动，默认为JDBC，不用改动，以后spring会接管
3. dataSource：连接池，也不用改动，spring会接管
4. typeAliases:起别名，如下：

```xml

<typeAliases>
    <package name="com.mybatis.pojo"/>
</typeAliases>
```

作用：扫描pojo下的实体类，在mapper.xml中方法的返回类型时就不需要写一长串，只需要实体类的类名即可(不区分大小写)

## 4.实现增删改查功能

### 4.1 通过配置文件

**注意：**
数据库表的字段名称和实体类的属性名称不一样时，不能自动封装数据
解决方案：

1. 起别名，查询时给字段加上as别名,让别名与实体类中的属性名一样即可。不推荐
2. 定义sql片段,缺点：不灵活。语法如下：

```xml

<sql id="brand_column">id,brand_name AS brandName</sql>
<select id="selectAll" resultType="brand">
Select
<include refield="brand_column"/>
From tb_brand;
</select>
```

3. 使用resultMap:定义<resultMap>标签，在<select>标签中使用resultMap来替换resultType属性

**说明：**

1. id:是唯一标识，type：映射类型，支持别名
2. 子标签：
    - result:完成一般字段的映射
    - id : 完成主键字段的映射
        - column : 数据库表的字段名
        - property : 实体类中的属性名

```xml

<resultMap id="brandResultMap" type="brand">
    <result column="brand_name" property="brandName"/>
    <result column="company_name" property="companyName"/>
</resultMap>
<select id="selectAll" resultMap="brandResultMap">
SELECT *
FROM tb_brand;
</select>
```

#### 4.1.1 参数查询

**参数占位符：**

1. #{}:相当于preparedStatement中的？占位符，目的是为了防止SQL注入
2. ${}：拼SQL，会存在SQL注入的问题
3. 使用时机：
    - 参数传递的时候：#{}
    - 表明/列名不固定的情况下：${} 但是会存在注入问题

**参数类型：**

1. parameterType:可以忽略不写
2. **特殊字符的处理：**
    1. 转义字符：< == &lt
    2. CDATA区：使用CD，自动补全格式，在[]中填写特殊字符，如：<

**步骤：**

1. 编写接口方法：Mapper接口
    - 参数：id
    - 结果：brand
2. 编写SQL语句，SQL映射文件
3. 执行方法，测试

**示例代码：**

```java
Brand selectById(int id);
```

```xml

<select id="selectById" parameterType="int" resultType="brand">
    select * from tb_brand where id = #{id};
</select> 
```

### 4.1.2 条件查询

#### 多条件查询-接收多个参数

**参数接收：**

1. **散装参数**：如果方法中有多个参数，需要使用`@Param("SQL参数占位符名称")`
2. **对象参数**：对象的属性名称要和参数占位符名称一致
3. **map集合参数**：保证SQL参数名和map集合的键的名称对应上，即可。

```java
//使用注解来添加参数
List<Brand> selectByCondition(@Param("status") int status,
                              @Param("companyName") String companyName,
                              @Param("brandName") String brandName);

// 直接传递对象即可
List<Brand> selectByCondition(Brand brand);

List<Brand> selectByCondition(Map map);
```

```xml

<select id="selectByCondition" resultMap="brandResultMap">
    SELECT * FROM tb_brand
    WHERE status = #{status}
    AND company_nmae LIKE #{companyName}
    AND brand_name LIKE #{brandName}
</select>
```

#### 多条件查询 -- 动态条件查询

**动态SQL:** SQL语句会随着用户的输入或外部条件的变化而变化

MyBatis对动态SQL有很强大的支撑；

1. if
2. choose(when,otherwise)
3. trim(where,set)
4. foreach

##### if判断：

**说明：**

1. 使用if标签，其中test是判断条件
2. test中的与为英文单词and
3. 判断的值应该为参数名称

```xml

<select id="selectByCondition" resultMap="brandResultMap">
    SELECT * FROM tb_brand
    WHERE
    <if test="status !=null">
        status = #{status}
    </if>
    <if test="companyName != null and companyName != ''">
        AND company_name LIKE #{companyName}
    </if>
    <if test="brandName!= null and brandName!=''">
        AND brand_name LIKE #{brandName}
    </if>
</select>
```