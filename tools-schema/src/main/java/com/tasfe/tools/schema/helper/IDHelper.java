package com.tasfe.tools.schema.helper;

/**
 * 序列生成器
 * Created by Lait on 2017/7/28.
 */
public class IDHelper {

    public static final String COLUMN = "column";
    public static final String TABLE = "table";

    private IDHelper() {

    }

    private static class ColumnIDInstance {
        private static final ID instance = new ID();
    }

    private static class TableIDInstance {
        private static final ID instance = new ID();
    }

    public static ID getInstance(String type) {
        if (IDHelper.COLUMN.equals(type)) {
            return ColumnIDInstance.instance;
        }
        if (IDHelper.TABLE.equals(type)) {
            return TableIDInstance.instance;
        }
        return TableIDInstance.instance;
    }


}

class ID {

    private int id = 0;

    public int getNext() {
        return ++id;
    }

}