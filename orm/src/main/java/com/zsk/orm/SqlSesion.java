package com.zsk.orm;

import com.zsk.annotation.Delete;
import com.zsk.annotation.Insert;
import com.zsk.annotation.Select;
import com.zsk.annotation.Update;
import com.zsk.uilt.MyIocSpring;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.List;

/**
 * 声明对外使用的方法
 */
public class SqlSesion {
    private SqlConfig sqlConfig = (SqlConfig) MyIocSpring.getBean("com.zsk.orm.SqlConfig");

    private  <T> T selectOne(String sql, Object obj, Class resultType) {
        return sqlConfig.serviceSelect(sql, obj, resultType);
    }

    private  <T> T selectOne(String sql, Class resultType) {
        return sqlConfig.serviceSelect(sql, null, resultType);
    }

    private  <T> List<T> selectTwo(String sql, Object obj, Class resultType) {
        return sqlConfig.serviceSelectMore(sql, obj, resultType);
    }

    private <T> List<T> selectTwo(String sql, Class resultType) {
        return sqlConfig.serviceSelectMore(sql, null, resultType);
    }

    private void insert(String sql, Object obj) {
        sqlConfig.serviceUpdate(sql, obj);
    }

    private void delece(String sql, Object obj) {
        sqlConfig.serviceUpdate(sql, obj);
    }

    private void update(String sql, Object obj) {
        sqlConfig.serviceUpdate(sql, obj);
    }

    private void insert(String sql) {
        sqlConfig.serviceUpdate(sql, null);
    }

    private void delece(String sql) {
        sqlConfig.serviceUpdate(sql, null);
    }

    private void update(String sql) {


        sqlConfig.serviceUpdate(sql, null);
    }

    /**
     * 动态代理匿名内部类类
     *
     * @param clazz 代理对象
     * @return obj
     */
    public Object getMapper(Class clazz) {
        // 类加载器
        ClassLoader classLoader = clazz.getClassLoader();
        //代理的接口是谁 通常数组就一个长度
        final Class[] classes = new Class[]{clazz};
        // 创建代理对象 obj 为代理对象
        Object obj = Proxy.newProxyInstance(classLoader, classes, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //获取方法上的注解
                Annotation annotation = method.getAnnotations()[0];
                //获取注解类型
                Class classType = annotation.annotationType();
                //获取方法
                Method valueMethod = classType.getDeclaredMethod("value");
                //获取sql
                String sql = (String) valueMethod.invoke(annotation);
                Object param = args == null ? null : args[0];
                if (classType == Insert.class) {
                    SqlSesion.this.insert(sql, param);
                } else if (classType == Update.class) {
                    SqlSesion.this.update(sql, param);
                } else if (classType == Delete.class) {
                    SqlSesion.this.delece(sql, param);
                } else if (classType == Select.class) {
                    //获取返回值类型
                    Class returnType = method.getReturnType();
                    if (returnType == List.class) {
                        //获取返回值类型带泛型的 多态的效果
                        Type genericReturnType = method.getGenericReturnType();
                        //转化为真实类
                        ParameterizedType realReturnType = (ParameterizedType) genericReturnType;
                        //反射这个所有泛型
                        Type[] typeArguments = realReturnType.getActualTypeArguments();
                        //只要第一个集合里的泛型
                        Type patternType = typeArguments[0];
                        //将这个泛型转换为Class
                        Class realPatternType = (Class) patternType;
                        //执行操作返回多条
                        return SqlSesion.this.selectTwo(sql, param, realPatternType);

                    } else {
                        //返回单条
                        return SqlSesion.this.selectOne(sql, param, returnType);
                    }
                }
                return null;
            }
        });
        return obj;
    }
}
