package com.tasfe.tools.schema.entity;

/**
 * 数据库连接配置实体类
 * Created by Lait on 2017/7/28.
 */

public class Configuration {
    private String url;
    private String user;
    private String pwd;
    private String dbName;

    public Configuration(String url, String user, String pwd) {
        super();
        this.url = url;
        this.user = user;
        this.pwd = pwd;
    }

    public Configuration(String url, String user, String pwd, String dbName) {
        super();
        this.url = url;
        this.user = user;
        this.pwd = pwd;
        this.dbName = dbName;
    }

    public String getUrl() {
        if (dbName != null) {
            return url + ";databaseName=" + dbName;
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}