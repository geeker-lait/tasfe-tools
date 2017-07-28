package com.tasfe.tools.schema.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据表实体类
 * Created by Lait on 2017/7/28.
 */
public class Table {
    //id
    private int id;
    //包名
    private String packName;
    //表名
    private String tableName;
    //中文表名
    private String tableName_ch;
    //备注
    private String description;

    private Map<String, Column> columns = new HashMap<String, Column>();

    public Table() {
    }

    public Table(String name) {
        this.tableName = name;
    }

    public Table(String packName, String tableName, String tableName_ch, String description) {
        super();
        this.packName = packName;
        this.tableName = tableName;
        this.tableName_ch = tableName_ch;
        this.description = description;
    }

    public Table(int id, String packName, String tableName, String tableName_ch, String description) {
        super();
        this.id = id;
        this.packName = packName;
        this.tableName = tableName;
        this.tableName_ch = tableName_ch;
        this.description = description;
    }

    public void addColumn(String name, Column column) {
        this.columns.put(name, column);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, Column> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, Column> columns) {
        this.columns = columns;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableName_ch() {
        return tableName_ch;
    }

    public void setTableName_ch(String tableName_ch) {
        this.tableName_ch = tableName_ch;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().toString().equals(this.getClass().toString())) {
            return false;
        }
        Table t = (Table) obj;
        if (t.getPackName().equals(this.getPackName()) && t.getTableName().equals(this.tableName)
                && t.getTableName_ch().equals(this.tableName_ch)) {
            return true;
        }
        return false;
    }
}