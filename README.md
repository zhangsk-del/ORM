# 基于连接池的ORM

## 为什么封装？

**连接池使用流程**

~~~java
 //获取连接
 Connection conn = ConnectionPool.getPool().getConnection();
 //创建状态参数
 PreparedStatement pstat = conn.prepareStatement(sql);
 //执行数据库操作
 pstat.executeQuery();
 //关闭连接 实际上不是真正的关闭
 conn.close();
~~~

有了连接池以后，我们JDBC使用的流程其实没有太多变化,JDBC的流程写在DAO类中，而DAO类中通常的方法都是一些CURD，都是重复性的代码。比如增删改三个方法 都是对数据库的更新操作，三个方法非常像，不同的是条SQL语句 ，差SQL上的那些问号信息

**封装ORM需要解决的问题**：解决DAO层代码**冗余**的问题，让操作数据库变得更简单，**实现在dao层方法上顶一注解即可**
## ORM调用流程

![](https://img-blog.csdnimg.cn/20210110151316881.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3oxMjQ1Njc4NDU1NA==,size_16,color_FFFFFF,t_70#pic_center)

## **封装实现**：

1、Service正常写，里面需要一个dao作为属性，可以是一个真实类  可以是一个代理
2、Dao如果是真实类，里面需要一个Sqlsession属性
3、Dao如果是接口类，里面只需要写方法 ，方法上面写注解  注解里面写SQL ，方法传参数即可


## 项目描述：

本项目是基于阿里巴巴的**druid**连接池进行封装的**ORM**，是在连接池上进一步的封装，为了加强自己的基础和能更深入解框架的本质， 自己实现了ORM框架的常用CRUD功能。本质是解决**DAO层代码冗余**的问题。

## 项目核心
核心动态代理类：/src/main/java/com/zsk/orm/**Sqlsession**.java下的getMapper方法

核心逻辑在该类：/src/main/java/com/zsk/orm/**Handler**.java 为核心处理类，该类下有方法的详细的说明，想深入理解的同学可以看该类下的源码哦

## 使用ORM(与MyBatis使用类似)：

```JAVA
 1、导入依赖包
<dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
        <version>5.1.47</version>
 </dependency>

   <!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
 <dependency>
      <groupId>com.alibaba</groupId>
      <artifactId>druid</artifactId>
      <version>1.1.20</version>
  </dependency>
     
  2、添加druid连接池配置文件,在classpath目录下创建druid.properties文件
     driverClassName=com.mysql.jdbc.Driver
     url=jdbc:mysql://localhost:3306/atm
     password=root
     username=root
     initialSize=5
     maxActive=20
     maxWait=3000
  
   3、在dao接口中写方法，在方法上写注解，注解内容为sql
    @Update("update atm set apassword=#{apassword} where id=2")
    void update(String apassword);

    @Insert("insert into atm(apassword,balance) values(#{apassword},#{balance})")
    void insert(Atm atm);

    @Delete("delete from atm where id=#{id}")
    void delete(Atm atm);

    @Select("select * from atm where #{id}")
    Atm select1(Atm atm);

    @Select("select * from atm")
    List<Atm> select2();

    @Select("select * from atm where id=#{id}")
    Atm select3(Map<String, String> map);

    @Select("select * from atm where id=#{id}")
    HashMap<String, String> select4(Map<String, String> map);
   
   4、在service中 new SqlSesion.getMapper(Dao接口.class)对象，有了该对象将可以使用ORM啦
     Dao接口 = SqlSesion sqlSesion=new SqlSesion().getMapper(Dao接口.class);
      
```






​		



