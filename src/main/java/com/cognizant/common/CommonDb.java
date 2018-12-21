package com.cognizant.common;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @author Abel
 * @date 2018/12/19 13:45
 */
public interface CommonDb {

    Connection getConnection();

    String sqlForCreateTable(Map<String, List<List<String>>> listMap, String key, String tableName);

    String sqlForInsert(Map<String, List<List<String>>> listMap, String key, String tableName);

    boolean insertBatch(String sql, List<List<String>> list);

    void createTable(String sql);


}
