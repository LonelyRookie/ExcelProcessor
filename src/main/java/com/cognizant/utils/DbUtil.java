package com.cognizant.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Abel
 * @date 2018/12/12 16:12
 */
public class DbUtil {

//    static final String URL = "jdbc:mysql://localhost:3306/excel_processor?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
//    static final String USERNAME = "root";
//    static final String PASSWORD = "root";


    /**
     * 连接数据库
     *
     * @return Connection
     */
    public static Connection getConnection() {
        try {
            String db_url = PropertyUtil.getProperty("db_url");
            String db_username = PropertyUtil.getProperty("db_username");
            String db_password = PropertyUtil.getProperty("db_password");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(db_url, db_username, db_password);
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("连接数据库失败");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        System.out.println("连接数据库失败");
        return null;
    }

    /**
     * 批量插入数据到数据库
     *
     * @param sql  sql语句
     * @param list List<List<String>>
     * @return boolean
     */
    public static boolean insertBatch(String sql, List<List<String>> list) {
        System.out.println("正在写入数据......");
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            for (int i = 1; i < list.size(); i++) {

                List<String> stringList = list.get(i);
                for (int y = 0; y < stringList.size(); y++) {
//                    statement.setString(y + 1, stringList.get(y));
                    statement.setString(y + 1, stringList.get(y));
                }
                statement.addBatch();
            }
            statement.executeBatch();
//            connection.commit();
            System.out.println("数据写入成功");
            return true;
        } catch (SQLException e) {
            System.out.println("数据写入失败");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("数据写入失败");
        return false;
    }


    /**
     * 生成数据库表
     *
     * @param sql sql语句
     */
    public static void createTable(String sql) {
        PreparedStatement pst = null;
        Connection connection = null;
        try {
            connection = getConnection();
            pst = connection.prepareStatement(sql);
            pst.executeUpdate();
            System.out.println("表创建成功");
        } catch (SQLException e) {
            System.out.println("表创建失败");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return;
        } finally {
            try {
                if (pst != null) pst.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 生成表格的sql
     *
     * @param listMap   Map<String, List<List<String>>>
     * @param key       Map对应的key
     * @param tableName 要生成的数据库表名
     * @return
     */
    public static String sqlForCreateTable(Map<String, List<List<String>>> listMap, String key, String tableName) {
        StringBuilder sb = new StringBuilder();
        List<List<String>> lists = listMap.get(key);
        List<String> titles = lists.get(0);

        String formatKey = CommonUtil.formatString(key).toLowerCase().trim();

        sb.append("CREATE TABLE IF NOT EXISTS " + "`" + CommonUtil.formatString(tableName) + "_" + formatKey + "`" + "(");
        sb.append("`id` int NOT NULL AUTO_INCREMENT,");
        for (int i = 0; i < titles.size(); i++) {
            String split = titles.get(i);
            String formatStr = CommonUtil.formatString(split);

            sb.append("`" + formatStr + i + "`" + " VARCHAR(255),");
        }
        sb.append("PRIMARY KEY(`id`)");
        sb.append(")CHARSET=utf8;");
        System.out.println(sb.toString());

        return sb.toString();
    }

    /**
     * 生成insert语句的sql
     *
     * @param listMap   Map<String, List<List<String>>>
     * @param key       Map对应的key
     * @param tableName 要生成的数据库表名
     * @return
     */
    public static String sqlForInsert(Map<String, List<List<String>>> listMap, String key, String tableName) {
        StringBuilder sb = new StringBuilder();
        List<List<String>> lists = listMap.get(key);
        List<String> fields = lists.get(0);

        String formatKey = CommonUtil.formatString(key).toLowerCase().trim();

        sb.append("insert into " + "`" + CommonUtil.formatString(tableName) + "_" + formatKey + "`" + "(");
        for (int i = 0; i < fields.size(); i++) {

            String split = fields.get(i);
            String formatStr = CommonUtil.formatString(split);

            if (i == fields.size() - 1) {
                sb.append(formatStr + i);
                continue;
            }
            sb.append(formatStr + i + ",");
        }
        sb.append(")");
        sb.append(" values(");
        for (int x = 0; x < fields.size(); x++) {
            if (x == fields.size() - 1) {
                sb.append("?");
                continue;
            }
            sb.append("?,");
        }
        sb.append(");");
        System.out.println(sb.toString());

        return sb.toString();

    }


}
