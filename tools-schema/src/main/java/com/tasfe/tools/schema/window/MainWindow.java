package com.tasfe.tools.schema.window;

import com.tasfe.tools.schema.core.EntityBuilder;
import com.tasfe.tools.schema.core.SyncDB;
import com.tasfe.tools.schema.entity.Configuration;
import com.tasfe.tools.schema.entity.Table;
import com.tasfe.tools.schema.helper.ConnectionHelper;
import com.tasfe.tools.schema.helper.DB;
import com.tasfe.tools.schema.helper.ExcelHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 主窗口类
 * Created by Lait on 2017/7/28.
 */
public class MainWindow extends java.awt.Frame {
    private static final long serialVersionUID = -4259339680151285634L;
    //时间格式
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    //文件选择器
    FileDialog fileDialog = null;
    Map<String, Table> excelTables = new HashMap<String, Table>();
    //列名
    private String[] columnStrs = new String[]{"", "表名", "中文名", "包名", ""};
    //连接信息
    private Configuration con;

    public MainWindow() {
        initComponents();
        initWindow();
    }

    /**
     * 初始化窗口
     */
    private void initWindow() {
        //不可修改大小
        this.setResizable(false);
        this.setTitle("excel2code");
        this.setSize(640, 480);

        //初始化文件选择器
        fileDialog = new FileDialog(this);
        //多选
        fileDialog.setMultipleMode(true);

        //初始化窗口位置
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

        //初始化表格
        initTabTable(null);

        print("根目录:" + getProjectPaht());
    }

    /**
     * 测试数据库连接
     */
    private void btnTestConActionPerformed(java.awt.event.ActionEvent evt) {
        String url = txtURL.getText();
        String user = txtUser.getText();
        String pwd = txtPassword.getText();
        String dbName = txtDB.getText();

        con = new Configuration(url, user, pwd);
        if (ConnectionHelper.testConnection(con)) {
            print("连接成功.");
        } else {
            print("连接失败.");
        }

        con.setDbName(dbName);

    }

    /**
     * 点击同步
     */
    private void btnBuildingActionPerformed(java.awt.event.ActionEvent evt) {
        //没有选中的行
        if (!isSelect()) {
            return;
        }

        //有选中的行
        print("开始同步...");
        print("开始测试连接...");

        String url = txtURL.getText();
        String user = txtUser.getText();
        String pwd = txtPassword.getText();
        String dbName = txtDB.getText();

        con = new Configuration(url, user, pwd);

        //测试连接
        boolean isConOk = ConnectionHelper.testConnection(con);
        if (!isConOk) {
            print("连接失败");
            print("同步失败");
            return;
        }
        print("连接成功");
        con.setDbName(dbName);
        print("正在初始化数据库...");
        DB.getInstance(url).initDB(url, user, pwd);
        print("数据库初始化完毕..");

        //开始同步表
        String filePath = getProjectPaht() + "createTable.sql";

        Map<String, Table> excelTemps = new HashMap<String, Table>();
        for (int i = 0; i < tabTables.getRowCount(); i++) {
            Boolean isSelect = (Boolean) tabTables.getModel().getValueAt(i, 0);
            if (isSelect) {
                Table t = (Table) tabTables.getModel().getValueAt(i, 4);
                excelTemps.put(t.getTableName(), t);
            }
        }
        //同步库
        SyncDB.builderTable(this, con, excelTemps);
        //生成sql脚本
        SyncDB.builderSql(this, con, excelTemps, filePath);

        print("同步完成\r\n");
    }

    /**
     * 点击生成实体
     */
    private void btnBuildEntityActionPerformed(java.awt.event.ActionEvent evt) {
        Map<String, Table> excelTemps = new HashMap<String, Table>();
        for (int i = 0; i < tabTables.getRowCount(); i++) {
            Boolean isSelect = (Boolean) tabTables.getModel().getValueAt(i, 0);
            if (isSelect) {
                Table t = (Table) tabTables.getModel().getValueAt(i, 4);
                excelTemps.put(t.getTableName(), t);
            }
        }
        //生成实体
        EntityBuilder.builder(this, excelTemps);
        print("生成完成\r\n");
    }

    /**
     * 选择文件
     */
    private void btnSelectFileActionPerformed(java.awt.event.ActionEvent evt) {
        //显示文件选择器
        fileDialog.setVisible(true);
        File[] fs = fileDialog.getFiles();
        if (fs.length > 0) {
            //清空所有
            excelTables.clear();
            for (int i = 0; i < fs.length; i++) {
                try {
                    Map<String, Table> ts = ExcelHelper.getAllTables(fs[i], true);
                    excelTables.putAll(ts);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
            //将excelTables中的数据，添加到tabTabes控件中
            initTabTable(null);
        }
    }

    /**
     * 得到项目根目录
     */
    public static String getProjectPaht() {
        return System.getProperty("user.dir");
    }

    /**
     * 将excelTables中的数据，添加到tabTabes控件中
     *
     * @param str 过滤字符
     */
    private void initTabTable(String str) {
        tabTables.removeAll();
        ArrayList<Object[]> arrTemp = new ArrayList<Object[]>();
        for (String key : excelTables.keySet()) {
            //需要过滤
            if (str != null) {
                if (key.indexOf(str) < 0) {
                    continue;
                }
            }
            Table ttemp = excelTables.get(key);
            arrTemp.add(new Object[]{true, ttemp.getTableName(), ttemp.getTableName_ch(), ttemp.getPackName(), ttemp});
        }

        //构建数据数组
        Object[][] tarr = new Object[arrTemp.size()][5];
        for (int i = 0; i < tarr.length; i++) {
            for (int j = 0; j < 5; j++) {
                tarr[i][j] = arrTemp.get(i)[j];
            }
        }
        tabTables.setModel(new DefaultTableModel(tarr, columnStrs));

        //第一列为选择列
        JCheckBox c = new JCheckBox();
        tabTables.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JCheckBox se = new JCheckBox();
                if ("true".equals(value.toString())) {
                    se.setSelected(true);
                }
                return se;
            }
        });
        tabTables.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(c));
        setCW(0, 20);
        setCW(1, 100);
        setCW(2, 100);
        setCW(3, 200);
        setCW(4, 10);
    }

    /**
     * 设置列宽
     */
    private void setCW(int index, int width) {
        TableColumn c = tabTables.getColumnModel().getColumn(index);
        if (c != null) {
            c.setPreferredWidth(width);
            c.setMinWidth(width);
        }
    }

    /**
     * 判断tabTables是否有选中的行
     */
    private boolean isSelect() {
        for (int i = 0; i < tabTables.getRowCount(); i++) {
            Boolean isSelect = (Boolean) tabTables.getModel().getValueAt(i, 0);
            if (isSelect) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将信息输出到控制台
     */
    public void print(String str) {
        txtConsole.append(sdf.format(new Date()) + "-->" + str + "\r\n");
    }

    //==================================以下是编辑器生成代码============================

    //GEN-BEGIN:initComponents
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        btnSelectFile = new java.awt.Button();
        scrTables = new javax.swing.JScrollPane();
        tabTables = new javax.swing.JTable();
        btnBuilding = new java.awt.Button();
        labDBtitile = new java.awt.Label();
        labURL = new java.awt.Label();
        txtURL = new java.awt.TextField();
        labUser = new java.awt.Label();
        txtUser = new java.awt.TextField();
        labPassword = new java.awt.Label();
        txtPassword = new java.awt.TextField();
        btnTestCon = new java.awt.Button();
        txtConsole = new java.awt.TextArea();
        labDB = new java.awt.Label();
        txtDB = new java.awt.TextField();
        btnBuildEntity = new java.awt.Button();

        setBackground(java.awt.SystemColor.control);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        setLayout(null);

        btnSelectFile.setLabel("Select File");
        btnSelectFile.setName("");
        btnSelectFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectFileActionPerformed(evt);
            }
        });
        add(btnSelectFile);
        btnSelectFile.setBounds(10, 30, 70, 26);

        tabTables.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[]{"", "表名", "中文名", "包名", ""}));
        scrTables.setViewportView(tabTables);

        add(scrTables);
        scrTables.setBounds(10, 60, 420, 180);

        btnBuilding.setLabel("SyncDB");
        btnBuilding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuildingActionPerformed(evt);
            }
        });
        add(btnBuilding);
        btnBuilding.setBounds(90, 30, 70, 26);

        labDBtitile.setText("DB Configuration");
        add(labDBtitile);
        labDBtitile.setBounds(450, 60, 180, 22);

        labURL.setText("Url:");
        add(labURL);
        labURL.setBounds(450, 90, 80, 22);

        //SQLEXPRESS
        //jdbc:sqlserver://125.216.243.42:1433
        //jdbc:oracle:thin:@125.216.243.42:1521:orcl
        //jdbc:mysql://125.216.243.42:3306/test
        txtURL.setText("jdbc:mysql://125.216.243.42:3306/test");
        add(txtURL);
        txtURL.setBounds(530, 90, 100, 22);

        labUser.setText("User:");
        add(labUser);
        labUser.setBounds(450, 150, 80, 22);

        txtUser.setText("admin");
        add(txtUser);
        txtUser.setBounds(530, 150, 100, 22);

        labPassword.setText("Password:");
        add(labPassword);
        labPassword.setBounds(450, 180, 80, 22);

        txtPassword.setText("123456");
        add(txtPassword);
        txtPassword.setBounds(530, 180, 100, 22);

        btnTestCon.setLabel("Test");
        btnTestCon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestConActionPerformed(evt);
            }
        });
        add(btnTestCon);
        btnTestCon.setBounds(570, 210, 57, 26);
        add(txtConsole);
        txtConsole.setBounds(10, 250, 620, 220);

        labDB.setText("DataBase:");
        add(labDB);
        labDB.setBounds(450, 120, 80, 22);

        txtDB.setText("SqlServer Only");
        add(txtDB);
        txtDB.setBounds(530, 120, 100, 22);

        btnBuildEntity.setActionCommand("Generate Code");
        btnBuildEntity.setLabel("Generate Code");
        btnBuildEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuildEntityActionPerformed(evt);
            }
        });
        add(btnBuildEntity);
        btnBuildEntity.setBounds(170, 30, 100, 26);

        pack();
    }// </editor-fold>
    //GEN-END:initComponents

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    public static void run() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    //GEN-BEGIN:variables
    // Variables declaration - do not modify
    private java.awt.Button btnBuildEntity;
    private java.awt.Button btnBuilding;
    private java.awt.Button btnSelectFile;
    private java.awt.Button btnTestCon;
    private java.awt.Label labDB;
    private java.awt.Label labDBtitile;
    private java.awt.Label labPassword;
    private java.awt.Label labURL;
    private java.awt.Label labUser;
    private javax.swing.JScrollPane scrTables;
    private javax.swing.JTable tabTables;
    private java.awt.TextArea txtConsole;
    private java.awt.TextField txtDB;
    private java.awt.TextField txtPassword;
    private java.awt.TextField txtURL;
    private java.awt.TextField txtUser;
    // End of variables declaration//GEN-END:variables

}