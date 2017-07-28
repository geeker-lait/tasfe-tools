package com.tasfe.tools.schema.helper;

import com.tasfe.tools.schema.entity.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 数据库连接工具类
 * Created by Lait on 2017/7/28.
 */
public class ConnectionHelper {

    private static Connection con = null;

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");//MsSqlServer 2005+
            Class.forName("oracle.jdbc.driver.OracleDriver");//Oracle 10g+
            Class.forName("com.mysql.jdbc.Driver");//Mysql 5.5+
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 得到某个数据库的连接
     *
     * @param url  不包含databaseName的url
     * @param user 数据库用户
     * @param pwd  密码
     */
    public static Connection getCon(String url, String user, String pwd) throws SQLException {
        if (con == null) {
            con = DriverManager.getConnection(url, user, pwd);
        }
        return con;
    }

    /**
     * 得到数据库的连接,不一定要指定数据库
     *
     * @param config
     * @return
     * @throws SQLException
     */
    public static Connection getCon(Configuration config) throws SQLException {
        if (con == null) {
            con = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPwd());
        }
        return con;
    }

    /**
     * 测试是否能连上数据库服务器
     */
    public static boolean testConnection(Configuration config) {
        try {
            getCon(config);
            return true;
        } catch (Exception err) {
        }
        return false;
    }

    /**
     * 关闭连接
     */
    public static void CloseCon(Connection con) {
        if (con != null) {
            try {
//				con.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * 执行sql
     *
     * @param sql
     * @param conn
     * @return
     * @throws Exception
     */
    public static boolean execSql(String sql, Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement(sql);
        boolean result = false;
        try {
            ps.execute();

        } finally {
            ps.close();
        }
        return result;
    }
}
