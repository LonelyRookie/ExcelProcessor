package com.cognizant.utils;

import java.io.*;
import java.util.Properties;

/**
 * @author Abel
 * @date 2018/12/20 12:47
 */
public class PropertyUtil {

    private static Properties props;

    synchronized static private void loadProps() {
        props = new Properties();
        InputStream in = PropertyUtil.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            props.load(in);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getProperty(String key) {
        if (null == props) {
            loadProps();
        }
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        if (null == props) {
            loadProps();
        }
        return props.getProperty(key, defaultValue);
    }

    public static Object setProperty(String key, String value) {
        return props.setProperty(key, value);
    }

//    public static void store(String key, String value) {
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream("files.properties");
//            props.setProperty(key, value);
//            props.store(fos, "文件存储路径");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fos != null) fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
