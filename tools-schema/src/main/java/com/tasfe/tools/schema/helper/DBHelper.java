package com.tasfe.tools.schema.helper;

import com.tasfe.tools.schema.entity.Column;
import com.tasfe.tools.schema.entity.Configuration;
import com.tasfe.tools.schema.entity.Table;

import java.sql.SQLException;
import java.util.Map;

/**
 * 数据库操作接口
 * Created by Lait on 2017/7/28.
 */
public interface DBHelper {


    /**
     * 列集全的表名
     */
    public final static String SSHCOLUMNS = "columnMate";

    /**
     * 表集全的表名
     */
    public final static String SSHTABLES = "tableMate";

    /**
     * 初始化数据库
     */
    public void initDB(String url, String user, String pwd);

    /**
     * 得到数据库中的表,不包括列
     *
     * @throws SQLException
     */
    public Map<String, Table> getAllTables(Configuration config) throws SQLException;

    /**
     * 得到某一张表
     *
     * @param tableName  表名
     * @param readColumn 是否需要读取列信息
     */
    public Table getTable(Configuration config, String tableName, boolean readColumn);

    /**
     * 得到某表的所有字段
     *
     * @param tableName 表名
     */
    public Map<String, Column> getColumnsByTable(Configuration config, String tableName);

    /**
     * 创建数据库
     *
     * @throws Exception 执行出错
     */
    public void createDataBase(Configuration config);

    /**
     * 在数据库中创建一张表
     */
    public void createTable(Configuration config, Table table);

    /**
     * 修改表信息
     */
    public void updateTable(Configuration config, Table table, int id) throws Exception;

    /**
     * 添加一个新字段
     */
    public void createColumn(Configuration config, Column c) throws Exception;

    /**
     * 修改表信息
     */
    public boolean updateColumn(Configuration config, Column cDB, Column cExcel) throws Exception;

    /**
     * 删除一个字段
     */
    public void dropColumn(Configuration config, Column c) throws Exception;

    /**
     * 处理列信息
     */
    public String getCommonColumnSql(Column c);

}