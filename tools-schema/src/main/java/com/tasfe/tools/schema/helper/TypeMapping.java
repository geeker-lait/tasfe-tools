package com.tasfe.tools.schema.helper;

/**
 * 类型映射关系
 * Created by Lait on 2017/7/28.
 */
public class TypeMapping {
    public static String sqlToJava(String type) {
        if (type == null) {
            return null;
        }
        type = type.toLowerCase();
        if (type.equals("int")) {
            return "java.lang.Integer";
        } else if (type.equals("integer")) {
            return "java.lang.Integer";
        } else if (type.equals("varchar")) {
            return "java.lang.String";
        } else if (type.equals("char")) {
            return "java.lang.String";
        } else if (type.equals("nchar")) {
            return "java.lang.String";
        } else if (type.equals("nvarchar")) {
            return "java.lang.String";
        } else if (type.equals("text")) {
            return "java.lang.String";
        } else if (type.equals("ntext")) {
            return "java.lang.String";
        } else if (type.equals("tinyint")) {
            return "java.lang.Integer";
        } else if (type.equals("int")) {
            return "java.lang.Integer";
        } else if (type.equals("tinyint")) {
            return "java.lang.Integer";
        } else if (type.equals("smallint")) {
            return "java.lang.Integer";
        } else if (type.equals("bit")) {
            return "java.lang.Boolean";
        } else if (type.equals("number")) {
            return "java.lang.Long";
        } else if (type.equals("bigint")) {
            return "java.lang.Long";
        } else if (type.equals("float")) {
            return "java.lang.Double";
        } else if (type.equals("decimal")) {
            return "java.math.BigDecimal";
        } else if (type.equals("money")) {
            return "java.math.BigDecimal";
        } else if (type.equals("smallmoney")) {
            return "java.math.BigDecimal";
        } else if (type.equals("numeric")) {
            return "java.math.BigDecimal";
        } else if (type.equals("real")) {
            return "java.lang.Float";
        } else if (type.equals("uniqueidentifier")) {
            return "java.lang.String";
        } else if (type.equals("smalldatetime")) {
            return "java.util.Date";
        } else if (type.equals("datetime")) {
            return "java.util.Date";
        } else if (type.equals("date")) {
            return "java.util.Date";
        } else if (type.equals("sql_variant")) {
            return "java.lang.String";
        }
        return null;
    }

}
