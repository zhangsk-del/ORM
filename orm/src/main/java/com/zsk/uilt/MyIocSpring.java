package com.zsk.uilt;

import java.util.HashMap;

public class MyIocSpring {
    private static HashMap<String,Object> userBox=new HashMap();
    public  static Object getBean(String forName) {
        Object obj = null;
        try {
            obj = userBox.get(forName);
            if (obj == null) {
                Class clazz = Class.forName(forName);
                obj = clazz.newInstance();
                userBox.put(forName, obj);
            }else {
                return obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}



