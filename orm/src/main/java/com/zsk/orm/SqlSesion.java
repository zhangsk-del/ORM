package com.zsk.orm;

import com.zsk.uilt.MyIocSpring;

import java.util.List;

/**
 * 声明对外使用的方法
 */
public class SqlSesion {
    private SqlConfig sqlConfig= (SqlConfig) MyIocSpring.getBean("com.zsk.orm.SqlConfig");


    public  <T>T selectOne(String sql,Object obj,Class resultType){
        return sqlConfig.serviceSelect(sql,obj,resultType);
    }
    public  <T>T selectOne(String sql,Class resultType){
        return sqlConfig.serviceSelect(sql,null,resultType);
    }
    public  <T> List<T> selectTwo(String sql,Object obj,Class resultType){
        return sqlConfig.serviceSelectMore(sql,obj,resultType);
}
    public  <T> List<T> selectTwo(String sql,Class resultType){
        return sqlConfig.serviceSelectMore(sql,null,resultType);
    }
    public void insert(String sql,Object obj){
        sqlConfig.serviceUpdate(sql,obj);
    }
    public void delece(String sql,Object obj){
        sqlConfig.serviceUpdate(sql,obj);
    }
    public void update(String sql,Object obj){
        sqlConfig.serviceUpdate(sql,obj);
    }
    public void insert(String sql){
        sqlConfig.serviceUpdate(sql,null);
    }
    public void delece(String sql){
        sqlConfig.serviceUpdate(sql,null);
    }
    public void update(String sql){
        sqlConfig.serviceUpdate(sql,null);
    }
}
