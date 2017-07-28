package com.tasfe.tools.schema.core;

import com.tasfe.tools.schema.entity.Column;
import com.tasfe.tools.schema.entity.Configuration;
import com.tasfe.tools.schema.entity.Table;
import com.tasfe.tools.schema.helper.DB;
import com.tasfe.tools.schema.window.MainWindow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * 数据库同步类
 * Created by Lait on 2017/7/28.
 */
public class SyncDB {

    /**
     * 同步数据库
     *
     * @param window
     * @param config
     * @param excelTables
     */
    public static void builderTable(MainWindow window, Configuration config, Map<String, Table> excelTables) {
        for (String key : excelTables.keySet()) {
            Table tableExcel = excelTables.get(key);
            String pak = tableExcel.getPackName();
            if (pak == null || pak.length() == 0) {
                window.print("发布失败,表" + tableExcel.getTableName() + "缺少包名!");
                return;
            }

            //从数据库中取出表,括列
            Table tableDB = DB.getInstance(config.getUrl()).getTable(config, tableExcel.getTableName(), true);
            if (tableDB == null) {//数据库中不存在此表
                try {
                    DB.getInstance(config.getUrl()).createTable(config, tableExcel);
                    window.print("建表成功:" + tableExcel.getTableName());
                    tableDB = DB.getInstance(config.getUrl()).getTable(config, tableExcel.getTableName(), true);
                } catch (Exception err) {
                    window.print("建表失败:" + tableExcel.getTableName() + err.getMessage());
                    return;//建表失败
                }
            }

            //找出需要修改的表基本信息,两张表不一样
            if (!tableExcel.equals(tableDB)) {
                try {
                    DB.getInstance(config.getUrl()).updateTable(config, tableExcel, tableDB.getId());//修改表基本信息
                    window.print("修改表成功:" + tableExcel.getTableName());
                } catch (Exception err) {
                    window.print("修改表失败:" + tableExcel.getTableName() + err.getMessage());
                }
            }

            //表表比较，找出需要添加修改的字段
            Map<String, Column> excelColumns = tableExcel.getColumns();
            Map<String, Column> dbColumns = tableDB.getColumns();
            for (String cn : excelColumns.keySet()) {
                String cnTemp = cn.toLowerCase().equals("id") ? "id" : cn;
                //数据库中没有此字段,则添加
                if (!dbColumns.containsKey(cnTemp)) {
                    try {
                        DB.getInstance(config.getUrl()).createColumn(config, excelColumns.get(cn));
                        window.print("添加字段成功\t表:" + tableExcel.getTableName() + "\t列:" + cn);
                    } catch (Exception err) {
                        window.print("添加字段失败:" + tableExcel.getTableName() + err.getMessage());
                    }
                } else {
                    try {
                        boolean isOK = DB.getInstance(config.getUrl()).updateColumn(config, dbColumns.get(cnTemp), excelColumns.get(cn));
                        if (isOK) {
                            window.print("修改字段成功\t表:" + tableExcel.getTableName() + "\t列:" + cn);
                        }
                    } catch (Exception err) {
                        window.print("修改字段失败:" + tableExcel.getTableName() + err.getMessage());
                    }
                }
            }

            //表与表比较，找到数据库中存在，但excel不存在（即删除字段）
            for (String cn : dbColumns.keySet()) {
                if (!(excelColumns.containsKey(cn) || excelColumns.containsKey(cn.toUpperCase()))) {//excel中不存在数据库中存在的字段
                    try {
                        DB.getInstance(config.getUrl()).dropColumn(config, dbColumns.get(cn));
                        window.print("删除字段成功\t表:" + tableExcel.getTableName() + "\t列:" + cn);
                    } catch (Exception err) {
                        window.print("删除字段失败:" + tableExcel.getTableName() + err.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 生成建表脚本
     *
     * @param window
     * @param excelTables
     * @param filePath
     */
    public static void builderSql(MainWindow window, Configuration config, Map<String, Table> excelTables, String filePath) {
        StringBuffer sb = new StringBuffer();
        for (String key : excelTables.keySet()) {
            Table table = excelTables.get(key);
            sb.append("-------------create table " + table.getTableName() + " -------------" + DB.NL);
            sb.append("create table ").append(table.getTableName()).append(DB.NL);
            sb.append(DB.LS + DB.NL);

            int sum = 0;
            for (String ckey : table.getColumns().keySet()) {
                Column column = table.getColumns().get(ckey);
                sb.append(DB.getInstance(config.getUrl()).getCommonColumnSql(column));
                if (sum < table.getColumns().size() - 1) {
                    sb.append(",");
                }
                sb.append(DB.NL);
                sum++;
            }
            sb.append(DB.RS + DB.NL + "" + DB.NL + DB.NL);
        }

        //保存到文件中
        File file = new File(filePath);
        //如果文件不存在
        if (!file.exists()) {
            try {
                //先尝试生成目录
                new File(file.getParent()).mkdirs();
                //现生成文件
                file.createNewFile();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        //输出到流中
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            bw.write(sb.toString());
            bw.flush();
            bw.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
        window.print("建表脚本已保存至:" + filePath);
    }
}
