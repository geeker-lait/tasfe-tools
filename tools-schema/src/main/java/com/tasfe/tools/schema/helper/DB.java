package com.tasfe.tools.schema.helper;

/**
 * 数据库帮助类
 * Created by Lait on 2017/7/28.
 */
public class DB {

    public static final String ORACLE_PREFIX = "jdbc:oracle";
    public static final String MYSQL_PREFIX = "jdbc:mysql";
    public static final String SQL_SERVER_PREFIX = "jdbc:sqlserver";


    public final static String EN = " ";
    public final static String DS = ",";
    public final static String LS = "(";
    public final static String RS = ")";
    public final static String LM = "[";
    public final static String RM = "]";
    public final static String NL = "\r\n";

    private static class OracleDBHelperInstance {
        private static final DBHelper instance = new OracleDBHelper();
    }

    private static class MysqlDBHelperInstance {
        private static final DBHelper instance = new MysqlDBHelper();
    }

    private static class SqlServerDBHelperInstance {
        private static final DBHelper instance = new SqlServerDBHelper();

    }

    public static DBHelper getInstance(String type) {
        if (type.startsWith(ORACLE_PREFIX)) {
            return OracleDBHelperInstance.instance;
        }
        if (type.startsWith(MYSQL_PREFIX)) {
            return MysqlDBHelperInstance.instance;
        }
        if (type.startsWith(SQL_SERVER_PREFIX)) {
            return SqlServerDBHelperInstance.instance;
        }
        return MysqlDBHelperInstance.instance;
    }

    private DB() {

    }

}
