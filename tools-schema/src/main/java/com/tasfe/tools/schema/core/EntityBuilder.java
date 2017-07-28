package com.tasfe.tools.schema.core;

import com.tasfe.tools.schema.entity.Column;
import com.tasfe.tools.schema.entity.Table;
import com.tasfe.tools.schema.helper.JavaCodeHelper;
import com.tasfe.tools.schema.helper.TypeMapping;
import com.tasfe.tools.schema.window.MainWindow;

import java.util.Map;


/**
 * 实体生成类
 * Created by Lait on 2017/7/28.
 */
public class EntityBuilder {
    /**
     * 生成实体
     *
     * @param window
     * @param excelTables
     */
    public static void builder(MainWindow window, Map<String, Table> excelTables) {
        for (String tableKey : excelTables.keySet()) {
            Table table = excelTables.get(tableKey);

            //通过表名取得类[如:com.test.T_DB_pserson改为com.test.PsersonInfo]
            String className = JavaCodeHelper.getClassAllName(table.getPackName() + "." + table.getTableName());

            //初始化
            JavaCodeHelper codeFile = new JavaCodeHelper(className, false, table.getTableName_ch() + JavaCodeHelper.NEWLINE + "*" + className, table.getTableName());
            //默认构造方法
            codeFile.addConstructor(null);

            codeFile.implementInterface("java.io.Serializable");
            codeFile.implortPage("javax.persistence.Column");
            codeFile.implortPage("javax.persistence.Entity");
            codeFile.implortPage("javax.persistence.Table");
            codeFile.implortPage("javax.persistence.Id");
            codeFile.implortPage("javax.persistence.GeneratedValue");
            codeFile.implortPage("javax.persistence.GenerationType");

            //其他属性
            for (String key : table.getColumns().keySet()) {
                Column c = table.getColumns().get(key);
                String fk = c.getForeignKey();
                //关联字段
                if (fk != null && fk.length() > 0 && !"null".equals(fk)) {
                    String filedName = "";
                    if (c.getFiledName() != null) {
                        filedName = c.getFiledName().trim();
                    }
                    fk = fk.replace("[", "");
                    String fks[] = fk.split("]");
                    //类型
                    String fullType = JavaCodeHelper.getClassAllName(fks[0]);
                    //包入类型包
                    codeFile.implortPage(fullType);
                    String type = JavaCodeHelper.classNameSubPackage(fullType)[1];
                    codeFile.addField("private", type, filedName, ";");
                    //添加注解和注释
                    codeFile.insertDescription(filedName, c.getFiledName2(), true);
                    //get and set
                    codeFile.addGetterAndSetter(filedName, type);
                } else {//普通字段
                    String filedName = "";
                    if (c.getFiledName() != null) {
                        filedName = c.getFiledName().trim();
                    }
                    System.out.println(c.getTableName() + "--" + c.getFiledName2() + "--" + filedName + "--->" + c.getFiledType());
                    String fullType = TypeMapping.sqlToJava(c.getFiledType());
                    String type = JavaCodeHelper.classNameSubPackage(fullType)[1];
                    //包入类型包
                    codeFile.implortPage(fullType);
                    codeFile.addField("private", type, filedName, ";");
                    //添加注解和注释
                    codeFile.insertDescription(filedName, c.getFiledName2(), true);
                    //get and set
                    codeFile.addGetterAndSetter(filedName, type);
                }
            }

            try {
                //生成文件
                codeFile.buider();
                window.print("生成:" + className);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

}