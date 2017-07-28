package com.tasfe.tools.schema.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * 对应Mysql底层操作类<br>
 * 包括得到所有表及字段、得到与数据库连接
 * Created by Lait on 2017/7/28.
 */
public class MysqlDBHelper extends OracleDBHelper {


    /**
     * 初始化数据库
     *
     * @param url
     * @param user
     * @param pwd
     */
    @Override
    public void initDB(String url, String user, String pwd) {
        String sql = "SELECT COUNT(*) as count FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + SSHCOLUMNS + "'";
        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(url, user, pwd);

            //初始化两张元数据表
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String count = rs.getString("COUNT");
                if (Integer.parseInt(count) == 0) {
                    // 不存在SSHCOLUMNS表，则创建
                    sql = "create table " + SSHCOLUMNS + "(id int primary key,tableName varchar(200),fieldName varchar(200),";
                    sql += " fieldName2 varchar(200),dataType varchar(100),reference varchar(200),description varchar(500),fieldLength int)";
                    ConnectionHelper.execSql(sql, conn);// 创建表
                }

            }

            sql = "SELECT COUNT(*) as count FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + SSHTABLES + "'";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                String count = rs.getString("COUNT");
                if (Integer.parseInt(count) == 0) {
                    // 不存在SSHTABLES表，则创建
                    sql = "create table " + SSHTABLES + "(id int primary key,packName varchar(400),tableName varchar(200),tableName_ch ";
                    sql += " varchar(200),description varchar(500))";
                    ConnectionHelper.execSql(sql, conn);// 创建表
                }

            }
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.CloseCon(conn);
        }
    }


}
