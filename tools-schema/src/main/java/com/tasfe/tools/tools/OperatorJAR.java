package com.tasfe.tools.tools;

/**
 * @author lait.zhang@gmail.com
 * @Date 2016年8月19日-上午9:44:12
 * @Description:<br></br>
 */

import java.util.*;
import java.util.jar.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;

public class OperatorJAR { // 操作JAR文件的类
    public static void readJARList(String fileName) throws IOException {// 显示JAR文件内容列表
        JarFile jarFile = new JarFile(fileName); // 创建JAR文件对象
        Enumeration en = jarFile.entries(); // 枚举获得JAR文件内的实体,即相对路径
        System.out.println("文件名\t文件大小\t压缩后的大小");
        while (en.hasMoreElements()) { // 遍历显示JAR文件中的内容信息
            process(en.nextElement()); // 调用方法显示内容
        }
    }

    private static void process(Object obj) {// 显示对象信息
        JarEntry entry = (JarEntry) obj;// 对象转化成Jar对象
        String name = entry.getName();// 文件名称
        long size = entry.getSize();// 文件大小
        long compressedSize = entry.getCompressedSize();// 压缩后的大小
        System.out.println(name + "\t" + size + "\t" + compressedSize);
    }

    public static void readJARFile(String jarFileName, String fileName) throws IOException {// 读取JAR文件中的单个文件信息
        JarFile jarFile = new JarFile(jarFileName);// 根据传入JAR文件创建JAR文件对象
        JarEntry entry = jarFile.getJarEntry(fileName);// 获得JAR文件中的单个文件的JAR实体
        InputStream input = jarFile.getInputStream(entry);// 根据实体创建输入流
        readFile(input);// 调用方法获得文件信息
        jarFile.close();// 关闭JAR文件对象流
    }

    public static void readFile(InputStream input) throws IOException {// 读出JAR文件中单个文件信息
        InputStreamReader in = new InputStreamReader(input);// 创建输入读流
        BufferedReader reader = new BufferedReader(in);// 创建缓冲读流
        String line;
        while ((line = reader.readLine()) != null) {// 循环显示文件内容
            System.out.println(line);
        }
        reader.close();// 关闭缓冲读流
    }

    public static void main(String args[]) throws IOException {// java程序主入口处
        OperatorJAR j = new OperatorJAR();
        System.out.println("1.输入一个JAR文件(包括路径和后缀)");
        Scanner scan = new Scanner(System.in);// 键盘输入值
        String jarFileName = scan.next();// 获得键盘输入的值
        readJARList(jarFileName);// 调用方法显示JAR文件中的文件信息
        System.out.println("2.查看该JAR文件中的哪个文件信息?");
        String fileName = scan.next();// 键盘输入值
        readJARFile(jarFileName, fileName);// 获得键盘输入的值

        //////////////////////////////////////////////////////////////////////////////////////
        String inputJar = "";
        JarOutputStream jarOut = null;
        JarInputStream jarIn = null;
        FileInputStream fileIn = null;
        // inputJar已有的压缩文件
        File file = new File(inputJar);
        // 这样做会已有的压缩文件就会被覆盖了
        jarOut = new JarOutputStream(new FileOutputStream(inputJar));
        jarIn = new JarInputStream(new FileInputStream(file));
    }

    public static void addFilesToExistingZip(File zipFile, File[] files) throws IOException {
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();

        boolean renameOk = zipFile.renameTo(tempFile);
        if (!renameOk) {
            throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }
        byte[] buf = new byte[1024];

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean notInFiles = true;
            for (File f : files) {
                if (f.getName().equals(name)) {
                    notInFiles = false;
                    break;
                }
            }
            if (notInFiles) {
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(name));
                // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        // Compress the files
        for (int i = 0; i < files.length; i++) {
            InputStream in = new FileInputStream(files[i]);
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(files[i].getName()));
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();
        }
        // Complete the ZIP file
        out.close();
        tempFile.delete();
    }

    public void testUri() throws Exception {
        URL url = new URL("jar:file:E:/dubbo-2.5.3.jar!/META-INF/MANIFEST.MF");
        JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
        InputStream in = jarConnection.getInputStream();// ("!/META-INF/MANIFEST.MF"));
        // System.out.println(IOUtils.copy(in, System.out));
    }

    public void dd() throws IOException {
        String baseDirName = "";
        File baseDir = new File(baseDirName);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        } else {
            String[] filelist = baseDir.list();
            for (int i = 0; i < filelist.length; i++) {
                if (filelist[i].contains("txtjar")) {// 查找含有你的txt的jar包
                    JarFile localJarFile = new JarFile(new File(baseDirName + File.separator + filelist[i]));
                    Enumeration<JarEntry> entries = localJarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();
                        String entryName = jarEntry.getName();
                        if (jarEntry.isDirectory()) {
                            // System.out.println(entryName);
                        } else if (entryName.endsWith(".txt")) {
                            // 在此处找到你的txt进行处理 }
                        }
                    }
                }
            }
        }
    }

    /**
     * JDK提供了一个JarFile类用来处理Jar文件的，不过只提供了读的功能
     *
     * 你可以通过如下步骤实现你的目标： 1） 通过JarFile读出Jar包信息。 2） 根据读出的信息将Jar包解压至临时目录。 3）在临时目录里替换文件。 4） 通过RunTime.exex()执行控制台命令重新生成Jar包
     * 写个bat文件，bat调用jar命令解压，然后调用copy命令把图片拷贝到相应的文件夹，然后再调用jar归档,在java里用Runtime.getRuntime().exec调用bat
     *
     * @param zipFile
     * @param localPath
     * @param bufferSize
     */
    public static void unzip(ZipFile zipFile, String localPath, int bufferSize) {
        byte[] buffer = new byte[bufferSize];
        ZipInputStream zip = null;
        ZipEntry zipEntry = null;
        try {
            zip = new ZipInputStream(new FileInputStream(zipFile.getName()));
            while ((zipEntry = zip.getNextEntry()) != null) {
                File file = new File(localPath, zipEntry.getName()).getAbsoluteFile();
                System.out.println(file.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    file.createNewFile();
                    long size = zipEntry.getSize();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        int readLen = -1;
                        while (size != 0) {
                            readLen = zip.read(buffer);
                            size -= readLen;
                            if (readLen != -1) {
                                fos.write(buffer, 0, readLen);
                            }
                        }
                    } finally {
                        if (fos != null) {
                            fos.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zip != null) {
                    zip.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}