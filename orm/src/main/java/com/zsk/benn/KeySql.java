package com.zsk.benn;

import java.io.Serializable;
import java.util.List;

public class KeySql implements Serializable {
    /**
     * 存放从sql解析出来的key
     */
    private List<String> keyList;
    /**
     * 解析好的sql语句
     */
    private String sqlBuilder;

    public KeySql(){}

    public KeySql(List<String> keyList, String sqlBuilder){
        this.keyList=keyList;
        this.sqlBuilder=sqlBuilder;
    }

    public List<String> getKeyList() {
        return keyList;
    }

    public void setKeyList(List<String> keyList) {
        this.keyList = keyList;
    }

    public String getSqlBuilder() {
        return sqlBuilder;
    }

    public void setSqlBuilder(String sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }
}
