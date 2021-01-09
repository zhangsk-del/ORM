package com.zsk.orm;

import com.zsk.benn.KeySql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 逻辑处理类
 */
public class Handler {
    //小弟1号，解析sql语句,并获取语句中的key
    KeySql analyzeSql(String sql) {
        //String sql="insert into study values(#{id},#{sname})";
        List<String> keyList = new ArrayList();//存key
        StringBuilder sqlBuilder = new StringBuilder();//将解析的字符串拼接
        while (true) {
            int left = sql.indexOf("#{");
            int right = sql.indexOf("}");
            if (left != -1 && right > left) {
                sqlBuilder.append(sql.substring(0, left));//拼接前一部分
                String key = sql.substring(left + 2, right);//获取一个key
                sqlBuilder.append("?");
                keyList.add(key);
            } else {
                sqlBuilder.append(sql);
                break;
            }
            sql = sql.substring(right + 1);//将下一部分进行循环
        }
        return new KeySql(keyList, sqlBuilder.toString());
    }

    // 增删改 小弟2号，解析map集合
    private void anlanMap(PreparedStatement pstat, Object obj, List keyList) throws SQLException {
        Map map = (Map) obj;
        //规则：sql的key----须与map集合的key对应且个数相同
        //通过list集合获取key,找对应的map集合的value,并赋值操作
        for (int i = 0; i < keyList.size(); i++) {//遍历map集合，通过key 找value
            String key = (String) keyList.get(i);
            String value = (String) map.get(key);
            pstat.setObject(i + 1, value);
        }
    }

    // 增删改 小弟3号，解析domain对象
    private void anlanDomain(PreparedStatement pstat, Object obj, List keyList) throws SQLException {
        //来的key遍历 需要将keyList中解析出反射去domain对象中找属性 获取属性value 赋值到SQL上拼接
        try {
            Class clazz = obj.getClass();
            for (int i = 0; i < keyList.size(); i++) {
                String key = (String) keyList.get(i);
                //获取私有属性名
                Field file = clazz.getDeclaredField(key);
                //获取属性的全名
                String name = file.getName();
                //拼接get方法
                StringBuilder builder = new StringBuilder("get");
                builder.append(name.substring(0, 1).toUpperCase());
                builder.append(name.substring(1));
                //获取get方法
                Method method = clazz.getMethod(builder.toString());
                //执行方法，获取属性值
                Object value = method.invoke(obj);
                //执行赋值操作
                pstat.setObject(i + 1, value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    //对sql中的？号赋值
    void houalPrepared(PreparedStatement pstat, Object obj, List keyList) throws SQLException {
        //keyLisl对象存着sql解析出来许多key
        Class clazz = obj.getClass();
        //基本类型+包装类+String
        //如传的为只有一个单独的类型，则直接执行赋值操作，map.domain对象须解析
        if (clazz == Integer.class || clazz == int.class) {
            pstat.setInt(1, (Integer) obj);
        } else if (clazz == Float.class || clazz == float.class) {
            pstat.setFloat(1, (Float) obj);
        } else if (clazz == double.class || clazz == Double.class) {
            pstat.setDouble(1, (Double) obj);
        } else if (clazz == String.class) {
            pstat.setString(1, (String) obj);
        } else if (clazz.isArray()) {
            // -----------------------------------------------------------
        } else {
            if (obj instanceof Map) {//map
                this.anlanMap(pstat, obj, keyList);
            } else {//domain
                this.anlanDomain(pstat, obj, keyList);
            }
        }
    }

    //需要结果集和返回值类型
    public Object handlerSelect(ResultSet rs, Class resultType) throws SQLException {
        //获取obj类型
        Object result = null;
        //获取单个返回值结果的
        if (resultType == Integer.class || resultType == int.class) {
            result = rs.getInt(1);
        } else if (resultType == Float.class || resultType == float.class) {
            result = rs.getFloat(1);
        } else if (resultType == Double.class || resultType == double.class) {
            result = rs.getDouble(1);
        } else if (resultType == String.class) {
            result = rs.getString(1);
        } else {
            try {
                //创建对象
                result = resultType.newInstance();

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (resultType == HashMap.class) {//MAP获取单条结果
                result = this.anlanSetqaMap(rs);
            } else {//domain
                result = this.anlanSetDomain(rs, result);

            }
        }
        return result;
    }

    // (查)小弟2号，解析domain
    public <T> T anlanSetDomain(ResultSet rs, Object result) throws SQLException {
        Class clazz = result.getClass();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                //获取每一个列名字
                String key = metaData.getColumnName(i);
                Object value = rs.getObject(key);
                //获取属性名
                Field filed = clazz.getDeclaredField(key);
                String name = filed.getName();
                //拼接set方法
                StringBuilder builder = new StringBuilder("set");
                builder.append(name.substring(0, 1).toUpperCase());
                builder.append(name.substring(1));
                //获取set方法和属性类型
                Method method = clazz.getDeclaredMethod(builder.toString(), filed.getType());
                //执行set方法
                method.invoke(result, value);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return (T) result;

    }

    //(查）小弟1号，解析map集合
    public Map<String, Object> anlanSetqaMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap();
        ResultSetMetaData metaData = rs.getMetaData();//可以获取结果集中全部的信息(列名 值)
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            //获取一个列名
            String key = metaData.getColumnName(i);
            //获取一个值
            Object value = rs.getObject(key);
            map.put(key, value);
        }
        return map;
    }

}

