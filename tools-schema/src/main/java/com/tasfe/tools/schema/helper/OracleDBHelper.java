package com.tasfe.tools.schema.helper;

import com.tasfe.tools.schema.entity.Column;
import com.tasfe.tools.schema.entity.Configuration;
import com.tasfe.tools.schema.entity.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * 对应Oracle底层操作类<br>
 * 包括得到所有表及字段、得到与数据库连接
 * Created by Lait on 2017/7/28.
 */
public class OracleDBHelper implements DBHelper {
    public final static String EN = " ";
    public final static String DS = ",";
    public final static String LS = "(";
    public final static String RS = ")";
    public final static String LM = "[";
    public final static String RM = "]";
    public final static String NL = "\r\n";

    /**
     * 列集全的表名
     */
    public final static String SSHCOLUMNS = "COLUMNMATE";

    /**
     * 表集全的表名
     */
    public final static String SSHTABLES = "TABLEMATE";

    /**
     * 初始化数据库
     */
    public void initDB(String url, String user, String pwd) {
        String sql = "SELECT COUNT(*) as count FROM user_tables WHERE table_name = '" + SSHCOLUMNS + "'";
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
                    // 创建表
                    ConnectionHelper.execSql(sql, conn);
                }

            }

            sql = "SELECT COUNT(*) as count FROM user_tables WHERE table_name = '" + SSHTABLES + "'";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                String count = rs.getString("COUNT");
                if (Integer.parseInt(count) == 0) {
                    // 不存在SSHTABLES表，则创建
                    sql = "create table " + SSHTABLES + "(id int primary key,packName varchar(400),tableName varchar(200),tableName_ch ";
                    sql += " varchar(200),description varchar(500))";
                    // 创建表
                    ConnectionHelper.execSql(sql, conn);
                }

            }
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.CloseCon(conn);
        }
    }

    /**
     * 得到数据库中的表,不包括列
     *
     * @throws SQLException
     */
    public Map<String, Table> getAllTables(Configuration config) throws SQLException {
        Map<String, Table> tables = new HashMap<String, Table>();
        String sql = "SELECT id,packName,tableName,tableName_ch,description FROM  " + SSHTABLES;
        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(config);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String packName = rs.getString("packName");
                String tableName = rs.getString("tableName");
                String tableName_ch = rs.getString("tableName_ch");
                String description = rs.getString("description");
                // 从数据库读取出来的表名全改为小写
                tables.put(tableName, new Table(id, packName, tableName, tableName_ch, description));
            }
        } finally {
            ConnectionHelper.CloseCon(conn);
        }
        return tables;
    }

    /**
     * 得到某一张表
     *
     * @param config     配置信息
     * @param tableName  表名
     * @param readColumn 是否需要读取列信息
     */
    public Table getTable(Configuration config, String tableName, boolean readColumn) {
        Table table = null;
        String sql = "SELECT id,packName,tableName_ch,description FROM " + SSHTABLES + " where tableName='" + tableName + "'";
        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(config);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String packName = rs.getString("packName");
                String tableName_ch = rs.getString("tableName_ch");
                String description = rs.getString("description");
                table = new Table(id, packName, tableName, tableName_ch, description);
            }
            if (table != null && readColumn) {
                // 如果需要查列，则查出列集合
                table.setColumns(getColumnsByTable(config, tableName));
            }
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.CloseCon(conn);
        }
        return table;
    }

    /**
     * 得到某表的所有字段
     *
     * @param config    配置信息
     * @param tableName 表名
     * @return
     */
    public Map<String, Column> getColumnsByTable(Configuration config, String tableName) {
        Map<String, Column> columns = new HashMap<String, Column>();
        String sql = "SELECT id,fieldName,fieldName2,dataType,reference,description,fieldLength FROM " + SSHCOLUMNS + " where tableName='" + tableName + "'";
        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(config);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Column c = new Column();
                c.setId(rs.getInt("id"));
                // 表名
                c.setTableName(tableName);
                String fieldName = rs.getString("fieldName");
                // 如果是id列
                if (fieldName != null && "id".equals(fieldName.toLowerCase())) {
                    // 字段名
                    c.setFiledName("id");
                } else {
                    // 字段名
                    c.setFiledName(fieldName);
                }
                // 字段名
                c.setFiledName2(rs.getString("fieldName2"));
                // 类型
                c.setFiledType(rs.getString("dataType"));
                // 外键
                c.setForeignKey(rs.getString("reference"));
                // 说明
                c.setDesc(rs.getString("description"));
                // 长度
                c.setFiledLength(rs.getInt("fieldLength"));

                columns.put(c.getFiledName(), c);
            }
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.CloseCon(conn);
        }
        return columns;
    }

    // -----------------------以下是同步方法---------------------

    /**
     * 创建数据库
     *
     * @param config
     */
    public void createDataBase(Configuration config) {
        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(config);
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.CloseCon(conn);
        }
    }

    /**
     * 在数据库中创建一张表
     *
     * @param config
     * @param table
     */
    public void createTable(Configuration config, Table table) {
        if (table == null) {
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(config);
            StringBuffer sql = new StringBuffer();
            sql.append("create table ").append(table.getTableName()).append(LS);
            int index = 0;
            for (String key : table.getColumns().keySet()) {
                Column c = table.getColumns().get(key);
                sql.append(getCommonColumnSql(c));
                if (index < table.getColumns().size() - 1) {
                    // 加上,
                    sql.append(DS);
                }
                index++;
                // 添加到SSHCOLUMNS表中
                String inSql = "insert into " + SSHCOLUMNS + " values(" + IDHelper.getInstance(IDHelper.COLUMN).getNext() + ",'" + table.getTableName() + "','" + c.getFiledName() + "','" + c.getFiledName2() + "','"
                        + c.getFiledType() + "','" + c.getForeignKey() + "','" + c.getDesc() + "'," + c.getFiledLength() + ")";
                System.out.println(inSql);
                ConnectionHelper.execSql(inSql, conn);
            }
            sql.append(RS);
            System.out.println(sql);
            // 执行sql
            ConnectionHelper.execSql(sql.toString(), conn);

            sql.setLength(0);
            sql.append("insert into " + SSHTABLES + " values(" + IDHelper.getInstance(IDHelper.TABLE).getNext() + ",'").append(table.getPackName());
            sql.append("','").append(table.getTableName());
            sql.append("','").append(table.getTableName_ch());
            sql.append("','").append(table.getDescription()).append("')");
            System.out.println(sql);
            ConnectionHelper.execSql(sql.toString(), conn);
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.CloseCon(conn);
        }
    }

    /**
     * 修改表信息
     *
     * @param config
     * @param table
     * @param id
     * @throws Exception
     */
    public void updateTable(Configuration config, Table table, int id) throws Exception {
        if (table == null) {
            return;
        }
        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(config);
            StringBuffer sql = new StringBuffer();
            sql.append(" update ").append(SSHTABLES).append(" set ");
            sql.append(" packName='").append(table.getPackName());
            sql.append("' ,tableName='").append(table.getTableName());
            sql.append("' ,tableName_ch='").append(table.getTableName_ch());
            sql.append("' ,description='").append(table.getDescription());
            sql.append("' where id=").append(id);
            // 执行sql
            ConnectionHelper.execSql(sql.toString(), conn);
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.CloseCon(conn);
        }
    }

    /**
     * 添加一个新字段
     */
    public void createColumn(Configuration config, Column c) throws Exception {
        if (c == null) {
            return;
        }
        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(config);
            StringBuffer sql = new StringBuffer();
            sql.append("alter table ").append(c.getTableName()).append(" add ");
            sql.append(getCommonColumnSql(c));
            ConnectionHelper.execSql(sql.toString(), conn);

            String inSql = "insert into " + SSHCOLUMNS + " values(" + IDHelper.getInstance(IDHelper.COLUMN).getNext() + ",'" + c.getTableName() + "','" + c.getFiledName() + "','" + c.getFiledName2() + "','"
                    + c.getFiledType() + "','" + c.getForeignKey() + "','" + c.getDesc() + "')";
            ConnectionHelper.execSql(inSql, conn);
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.CloseCon(conn);
        }
    }

    /**
     * 修改表信息
     *
     * @param config
     * @param cDB
     * @param cExcel
     * @return
     * @throws Exception
     */
    public boolean updateColumn(Configuration config, Column cDB, Column cExcel) throws Exception {
        // 如果需要修改
        if (!cDB.equals(cExcel)) {
            Connection conn = null;
            try {
                conn = ConnectionHelper.getCon(config);
                StringBuffer sql = new StringBuffer();
                sql.append("alter table ").append(cExcel.getTableName()).append(" alter column ");
                sql.append(getCommonColumnSql(cExcel));
                ConnectionHelper.execSql(sql.toString(), conn);// 更新数据库

                String upSql = "update " + SSHCOLUMNS + " set fieldName2='" + cExcel.getFiledName2() + "',dataType='" + cExcel.getFiledType() + "',reference='"
                        + cExcel.getForeignKey() + "',description='" + cExcel.getDesc() + "',fieldLength=" + cExcel.getFiledLength() + "  where tableName='"
                        + cExcel.getTableName() + "' and fieldName='" + cExcel.getFiledName() + "'";
                ConnectionHelper.execSql(upSql, conn);// 修改SSHCOLUMNS
            } catch (Exception err) {
                err.printStackTrace();
            } finally {
                ConnectionHelper.CloseCon(conn);
            }
            return true;
        } else {
            return false;
        }

    }

    /**
     * 删除一个字段
     */
    public void dropColumn(Configuration config, Column c) throws Exception {
        Connection conn = null;
        try {
            conn = ConnectionHelper.getCon(config);
            if (c == null) {
                return;
            }
            String inSql = "delete from " + SSHCOLUMNS + " where id=" + c.getId();
            ConnectionHelper.execSql(inSql, conn);
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            ConnectionHelper.CloseCon(conn);
        }
    }

    /**
     * 处理列信息
     *
     * @param c
     * @return
     */
    public String getCommonColumnSql(Column c) {
        if (c == null) {
            return "";
        }
        StringBuffer sql = new StringBuffer();
        // 有长度
        sql.append(c.getFiledName()).append(EN).append(c.getFiledType());
        // 有长度
        if (c.isHasLength()) {
            // 有精度
            if (c.isHasPrecision()) {
                sql.append(LS).append(c.getFiledLength()).append(DS).append(c.getPrecision()).append(RS);
            } else {
                // 无精度
                sql.append(LS).append(c.getFiledLength()).append(RS);
            }
        }
        // id主键
        if (c.isPrimaryKey()) {
            sql.append(" primary key");
        }
        return sql.toString();
    }
}
