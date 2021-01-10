package com.zsk.orm;

import com.zsk.benn.KeySql;
import com.zsk.uilt.DruidUtil;
import com.zsk.uilt.MyIocSpring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlConfig {

    private static Handler handler = (Handler) MyIocSpring.getBean("com.zsk.orm.Handler");

    //设置一方法，进行增/删/改 操作
    //obj所携带的参数
    public static void serviceUpdate(String sql, Object obj) {
        Connection conn = null;
        PreparedStatement pstat = null;
        try {
            KeySql keySql = handler.analyzeSql(sql);
            conn = DruidUtil.getConnection();
            pstat = conn.prepareStatement(keySql.getSqlBuilder());
            if (obj != null) {
                handler.houalPrepared(pstat, obj, keySql.getKeyList());
            }
            pstat.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DruidUtil.close(conn, pstat, null);
        }
    }

    //单条查询语句
    public static <T> T serviceSelect(String sql, Object obj, Class resultType) {
        return (T) serviceSelectMore(sql, obj, resultType).get(0);
    }

    //多条语句
    public static <T> List<T> serviceSelectMore(String sql, Object obj, Class resultType) {
        List<T> list = new ArrayList();
        Connection conn = null;
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            KeySql keySql = handler.analyzeSql(sql);
            conn = DruidUtil.getConnection();
            pstat = conn.prepareStatement(keySql.getSqlBuilder());
            if (obj != null) {
                handler.houalPrepared(pstat, obj, keySql.getKeyList());
            }
            rs = pstat.executeQuery();
            while (rs.next()) {
                //设计一个方法 负责分析给定Class类型   确定返回值是什么类型
                list.add((T) handler.handlerSelect(rs, resultType));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DruidUtil.close(conn, pstat, rs);
        }
        return list;
    }


}
