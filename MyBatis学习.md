# MyBatis 学习

## 1. 配置相关信息

### 1.1 Maven 导入依赖

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
    <!-- 其他依赖的导入 -->
</dependencies>
```

### 1.2 创建 `mybatis-config.xml` 文件

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
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="${url}"/>
                <property name="password" value="${password}"/>
                <property name="username" value="${username}"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <!-- SQL 语句映射文件 -->
        <!-- 没有使用 Mapper 代理开发 -->
        <!-- <mapper resource="org/mybatis/example/BlogMapper.xml"/> -->
        <!-- 使用 Mapper 代理开发 -->
        <package name="com.mybatis.mapper"/>
    </mappers>
</configuration>
```

### 1.3 创建 `xxxMapper.xml` 配置文件

**说明：** 此文件名中的 xxx 是根据所要操作的数据表起的名称

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--
    根标签 mapper：namespace 指定当前 mapper 的命名空间
    - 当没有使用 Mapper 代理开发时，namespace 可以随意设置（如 test）
    - 使用 Mapper 代理开发时，需要写完整的包路径
-->
<mapper namespace="com.mybatis.mapper.UserMapper">
    <!-- 根据所要操作的表改写命名空间和编写 SQL 语句 -->
    <!-- select 标签中的 id 是要被引用的标识，相当于 PreparedStatement -->
    <select id="selectAll" resultType="com.mybatis.pojo.User">
        SELECT * FROM tb_user;
    </select>
    <!-- 在此编写增删改查的 SQL 语句 -->
</mapper>
```

### 1.4 在 main 方法中使用 MyBatis

拥有实体类之后，在 main 方法中编写以下代码：

```java
// 1. 加载 MyBatis 的核心配置文件，获取 SqlSessionFactory 对象
//    （不需要记忆，写一遍后直接复制即可）
String resource = "mybatis-config.xml";
InputStream inputStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

// 2. 获取 SqlSession 对象，用它来执行 SQL 语句
SqlSession sqlSession = sqlSessionFactory.openSession();

// 3. 执行 SQL 语句
// 方式一：使用 "命名空间.方法id" 的方式（不推荐）
List<User> users = sqlSession.selectList("test.selectAll");

// 方式二：使用 Mapper 代理开发（推荐）
UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
List<User> users = userMapper.selectAll();

System.out.println(users);

// 4. 释放资源
sqlSession.close();
```

## 2. Mapper 代理开发

### 2.1 目的

- 解决原生方式中的硬编码问题
- 简化后期执行 SQL 语句的操作

### 2.2 实现步骤

1. **定义 Mapper 接口**
   - 定义与 SQL 映射文件同名的 Mapper 接口
   - 将 Mapper 接口和 SQL 映射文件放置在统一目录下
   - 在 resources 中创建同样的包结构，如 `resources/com/mybatis/mapper`

2. **设置命名空间**
   - 设置 SQL 映射文件的 namespace 属性为 Mapper 接口全限定名
   - 例如：`com.mybatis.mapper.UserMapper`

3. **定义接口方法**
   - 在 Mapper 接口中定义方法
   - 方法名与 SQL 映射文件中 SQL 语句的 id 保持一致
   - 参数类型和返回值类型也要保持一致

4. **编写代码**
   - 通过 SqlSession 的 getMapper 方法获取 Mapper 接口的代理对象
   - 调用对应方法完成 SQL 的执行

```java
UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
List<User> users = userMapper.selectAll();
```

### 2.3 配置说明

当按照以上规则使用代理开发时，在 `mybatis-config.xml` 中的 SQL 映射文件部分可以使用包扫描的方式：

```xml
<!-- 方式一：指定具体的映射文件 -->
<mapper resource="com/mybatis/mapper/UserMapper.xml"/>

<!-- 方式二：使用包扫描（推荐） -->
<package name="com.mybatis.mapper"/>
```

## 3. 环境配置

在 `mybatis-config.xml` 中可以配置以下内容：

### 3.1 environments（环境配置）

配置数据库连接环境信息，可以有多个 environment，通过 default 属性切换不同的环境：

```xml
<!-- 默认数据源，可按需更改 -->
<environments default="development">
    <!-- 开发环境数据源 -->
    <environment id="development">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <property name="driver" value="com.mysql.jdbc.Driver"/>
            <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=false"/>
            <property name="password" value="password"/>
            <property name="username" value="root"/>
        </dataSource>
    </environment>

    <!-- 测试环境数据源 -->
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

### 3.2 transactionManager（事务管理器）

事务管理的驱动，默认为 JDBC，不用改动，以后 Spring 会接管。

### 3.3 dataSource（数据源）

连接池配置，也不用改动，Spring 会接管。

### 3.4 typeAliases（类型别名）

为实体类起别名，简化配置：

```xml
<typeAliases>
    <package name="com.mybatis.pojo"/>
</typeAliases>
```

**作用：** 扫描 pojo 包下的实体类，在 mapper.xml 中配置方法的返回类型时就不需要写完整的类路径，只需要写实体类的类名即可（不区分大小写）。

## 4. 实现增删改查功能

### 4.1 通过配置文件实现

#### 4.1.1 字段映射问题

**问题：** 当数据库表的字段名称和实体类的属性名称不一样时，不能自动封装数据。

**解决方案：**

**方案一：使用别名（不推荐）**

查询时给字段加上 AS 别名，让别名与实体类中的属性名一样即可。

**方案二：定义 SQL 片段（不够灵活）**

```xml
<sql id="brand_column">
    id, brand_name AS brandName, company_name AS companyName
</sql>

<select id="selectAll" resultType="brand">
    SELECT
    <include refid="brand_column"/>
    FROM tb_brand;
</select>
```

**方案三：使用 resultMap（推荐）**

定义 `<resultMap>` 标签，在 `<select>` 标签中使用 resultMap 来替换 resultType 属性。

**resultMap 说明：**

- `id`：唯一标识
- `type`：映射的实体类型，支持别名
- 子标签：
  - `<result>`：完成一般字段的映射
  - `<id>`：完成主键字段的映射
    - `column`：数据库表的字段名
    - `property`：实体类中的属性名

```xml
<resultMap id="brandResultMap" type="brand">
    <result column="brand_name" property="brandName"/>
    <result column="company_name" property="companyName"/>
</resultMap>

<select id="selectAll" resultMap="brandResultMap">
    SELECT * FROM tb_brand;
</select>
```

#### 4.1.2 参数查询

**参数占位符：**

1. `#{}`：相当于 PreparedStatement 中的 `?` 占位符，目的是为了防止 SQL 注入
2. `${}`：拼接 SQL，会存在 SQL 注入的问题
3. **使用时机：**
   - 参数传递时：使用 `#{}`
   - 表名/列名不固定的情况下：使用 `${}`（但会存在注入问题）

**参数类型：**

- `parameterType`：可以省略不写

**特殊字符的处理：**

1. 转义字符：`<` 使用 `&lt;` 表示
2. CDATA 区：使用 `<![CDATA[]]>` 包裹特殊字符

**实现步骤：**

1. 编写接口方法（Mapper 接口）
   - 参数：id
   - 结果：Brand
2. 编写 SQL 语句（SQL 映射文件）
3. 执行方法，测试

**示例代码：**

```java
Brand selectById(int id);
```

```xml
<select id="selectById" parameterType="int" resultType="brand">
    SELECT * FROM tb_brand WHERE id = #{id};
</select>
```

#### 4.1.3 条件查询

**多条件查询 - 接收多个参数**

参数接收方式：

1. **散装参数**：如果方法中有多个参数，需要使用 `@Param("SQL参数占位符名称")`
2. **对象参数**：对象的属性名称要和参数占位符名称一致
3. **Map 集合参数**：保证 SQL 参数名和 Map 集合的键的名称对应上即可

```java
// 方式一：使用注解来添加参数
List<Brand> selectByCondition(@Param("status") int status,
                              @Param("companyName") String companyName,
                              @Param("brandName") String brandName);

// 方式二：直接传递对象
List<Brand> selectByCondition(Brand brand);

// 方式三：使用 Map 集合
List<Brand> selectByCondition(Map map);
```

```xml
<select id="selectByCondition" resultMap="brandResultMap">
    SELECT * FROM tb_brand
    WHERE status = #{status}
      AND company_name LIKE #{companyName}
      AND brand_name LIKE #{brandName}
</select>
```

#### 4.1.4 动态条件查询

**动态 SQL：** SQL 语句会随着用户的输入或外部条件的变化而变化。

MyBatis 对动态 SQL 有很强大的支持：

1. `<if>`：条件判断
2. `<choose>`、`<when>`、`<otherwise>`：多条件选择
3. `<trim>`、`<where>`、`<set>`：辅助标签
4. `<foreach>`：循环遍历

**使用 if 标签进行条件判断**

**说明：**

1. 使用 `<if>` 标签，其中 `test` 是逻辑表达式
2. `test` 中的逻辑与使用英文单词 `and`
3. 判断的值应该为参数名称

**问题：**

当存在多个条件时，若第一个条件的参数为空，就会变成 `WHERE AND ...`，这样的语法不符合规则。

**解决方案一：增加恒等式**

```xml
<select id="selectByCondition" resultMap="brandResultMap">
    SELECT * FROM tb_brand
    WHERE 1 = 1
    <if test="status != null">
        AND status = #{status}
    </if>
    <if test="companyName != null and companyName != ''">
        AND company_name LIKE #{companyName}
    </if>
    <if test="brandName != null and brandName != ''">
        AND brand_name LIKE #{brandName}
    </if>
</select>
```

**解决方案二：使用 where 标签（推荐）**

通过 `<where>` 标签替换 WHERE 关键字，会自动处理多余的 AND。

```xml
<select id="selectByCondition" resultMap="brandResultMap">
    SELECT * FROM tb_brand
    <where>
        <if test="status != null">
            AND status = #{status}
        </if>
        <if test="companyName != null and companyName != ''">
            AND company_name LIKE #{companyName}
        </if>
        <if test="brandName != null and brandName != ''">
            AND brand_name LIKE #{brandName}
        </if>
    </where>
</select>
```

#### 4.1.5 单条件动态查询

**使用 choose、when、otherwise 标签**

**说明：** 从多个条件中选择一个，类似于 Java 中的 switch-case 语句。

```xml
<select id="selectByConditionSingle" resultMap="brandResultMap">
    SELECT * FROM tb_brand
    <where>
        <choose>  <!-- 相当于 Java 中的 switch -->
            <when test="status != null">  <!-- 相当于 case -->
                status = #{status}
            </when>
            <when test="companyName != null and companyName != ''">
                company_name = #{companyName}
            </when>
            <when test="brandName != null and brandName != ''">
                brand_name = #{brandName}
            </when>
            <!-- <otherwise> 标签相当于 default，可选 -->
        </choose>
    </where>
</select>
```

**说明：** 使用 `<where>` 标签包裹 `<choose>`，可以避免用户一个参数也没有传递时出现语法错误。

#### 4.1.6 添加功能

**实现步骤：**

1. 编写接口方法（Mapper 接口）
   - 参数：除了 id 之外的所有数据
   - 结果：void
2. 编写 SQL 语句（SQL 映射文件）
3. 执行方法，测试

**MyBatis 事务管理：**

- `openSession()`：默认开启事务，进行增删改操作后需要使用 `sqlSession.commit()` 手动提交事务
- `openSession(true)`：可以设置为自动提交事务（关闭事务）

**主键回显：**

在 `<insert>` 标签中添加 `useGeneratedKeys="true"` 和 `keyProperty="id"`，可以在插入后获取自增主键值。

```xml
<insert id="add" useGeneratedKeys="true" keyProperty="id">
    INSERT INTO tb_brand (brand_name, company_name, ordered, description, status)
    VALUES (#{brandName}, #{companyName}, #{ordered}, #{description}, #{status});
</insert>
```

#### 4.1.7 修改功能

**修改全部字段**

实现步骤与添加功能类似，使用 `<update>` 标签编写 UPDATE 语句即可。

**修改动态字段**

使用 `<set>` 和 `<if>` 标签来动态修改字段，只更新传入的非空字段。

```xml
<update id="updateNotAll">
    UPDATE tb_brand
    <set>
        <if test="brandName != null and brandName != ''">
            brand_name = #{brandName},
        </if>
        <if test="companyName != null and companyName != ''">
            company_name = #{companyName},
        </if>
        <if test="ordered != null">
            ordered = #{ordered},
        </if>
        <if test="description != null and description != ''">
            description = #{description},
        </if>
        <if test="status != null">
            status = #{status}
        </if>
    </set>
    WHERE id = #{id};
</update>
```

**说明：** `<set>` 标签会自动处理多余的逗号，避免 SQL 语法错误。

#### 4.1.8 删除功能

**删除单个记录**

实现步骤与查询类似，使用 `<delete>` 标签编写 DELETE 语句即可。

**批量删除**

**实现步骤：**

1. 编写接口方法
   - 参数：id 数组
   - 结果：void
2. 编写 SQL 语句（SQL 映射文件）
3. 执行方法，测试

**使用 foreach 标签遍历数组：**

- `collection`：遍历的数组
  - MyBatis 会将数组参数封装为一个 Map 集合：
    - 默认：`key=array`，`value=数组`
    - 使用 `@Param` 可以改变 Map 集合中默认的 key 名称，如 `collection='ids'`
- `item`：遍历时的元素变量名
- `separator`：元素之间的分隔符，使用 `,` 连接数组中的各个元素
- `open`/`close`：在开头和结尾添加的字符，如 `(` 和 `)`

```xml
<delete id="delete">
    DELETE FROM tb_brand
    WHERE id IN
    <foreach collection="array" item="id" separator="," open="(" close=")">
        #{id}
    </foreach>
</delete>
```

## 5. MyBatis 的参数传递

MyBatis 接口方法中可以接受各种各样的参数，底层对于这些参数会进行不同的封装处理方式。

### 5.1 单个参数

1. **POJO 类型**：直接使用，属性名和 SQL 参数的占位符名称一致
2. **Map 集合**：直接使用，键名和参数占位符名称一致即可
3. **Collection 集合**：封装为 Map 集合
   ```
   map.put("arg0", collection集合);
   map.put("collection", collection集合);
   ```
4. **List 集合**：封装为 Map 集合
   ```
   map.put("arg0", list集合);
   map.put("collection", list集合);
   map.put("list", list集合);
   ```
5. **Array 数组**：封装为 Map 集合
   ```
   map.put("arg0", 数组);
   map.put("array", 数组);
   ```
6. **其他类型**：直接使用

### 5.2 多个参数

多个参数会被封装为 Map 集合，可以使用 `@Param` 注解替换 Map 集合中默认的 arg 键名。

**默认封装方式：**

```
map.put("arg0", 参数值1);
map.put("param1", 参数值1);
map.put("arg1", 参数值2);
map.put("param2", 参数值2);
```

**使用 @Param 注解后：**

```java
User select(@Param("username") String username, String password);
```

```
map.put("username", 参数值1);  // arg0 被替换为 username
map.put("param1", 参数值1);
map.put("arg1", 参数值2);
map.put("param2", 参数值2);
```

### 5.3 不使用 @Param 注解的示例

当有多个参数时，也可以不使用 `@Param` 注解，直接使用 `param1`、`param2` 等：

```java
User select(String username, String password);
```

```xml
<select id="select" resultType="User">
    SELECT * FROM tb_user
    <where>
        <if test="param1 != null and param1 != ''">
            AND username = #{param1}  <!-- 也可以使用 #{arg0} -->
        </if>
        <if test="param2 != null and param2 != ''">
            AND password = #{param2}  <!-- 也可以使用 #{arg1} -->
        </if>
    </where>
</select>
```

### 5.4 参数封装原理

MyBatis 提供了 `ParamNameResolver` 类来进行参数的封装，这也是 `@Param` 注解的底层实现原理。

## 6. 使用注解来完成增删改查

使用注解会比使用配置文件更加方便，适合简单的 SQL 操作。

### 6.1 注解类型

- **@Select**：查询操作
- **@Insert**：添加操作
- **@Update**：修改操作
- **@Delete**：删除操作

### 6.2 示例代码

```java
@Select("SELECT * FROM tb_user WHERE id = #{id}")
public User selectById(int id);
```

### 6.3 使用建议

- **使用注解**：适合完成简单的功能，不能使用动态 SQL 语句
- **使用配置文件**：适合完成复杂功能，支持动态 SQL

---

**文档整理完成**