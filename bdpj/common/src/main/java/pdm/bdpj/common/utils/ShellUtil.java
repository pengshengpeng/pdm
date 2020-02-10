package pdm.bdpj.common.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * @Author pengshengpeng
 * @Date 2020/2/10 16:07
 **/
public class ShellUtil {
    /**
     * 创建执行sql的shell文件
     *
     * @param path      生成shell文件的存放目录
     * @param tableName sql表名
     * @throws Exception 异常
     */
    public static void createExcuteHiveShell(String path, String tableName) throws Exception {
        String fileName = "exec_" + tableName + "_yyyymmdd.sh";
        File sh = new File(path, fileName);
        if (sh.exists()) {
            sh.delete();
        }
        sh.createNewFile();
        sh.setExecutable(true);
        FileWriter fw = new FileWriter(sh);
        BufferedWriter bf = new BufferedWriter(fw);
        String[] hiveCommond = {"date=$1", "echo ${date}", "hive --hiveconf yyyymmdd='${date}' -f create_" + tableName + "_${date}.sql"};
        for (int i = 0; i < hiveCommond.length; i++) {
            bf.write(hiveCommond[i]);
            if (i < hiveCommond.length - 1) {
                bf.newLine();
            }
        }
        bf.flush();
        bf.close();
    }

    /**
     * 创建导入数据的shell文件
     *
     * @param path      生成shell文件的存放目录
     * @param tableName sql表名
     * @throws Exception 异常
     */
    public static void createPutHiveShell(String path, String tableName) throws Exception {
        String fileName = "put_" + tableName + "_yyyymmdd.sh";
        File sh = new File(path, fileName);
        if (sh.exists()) {
            sh.delete();
        }
        sh.createNewFile();
        sh.setExecutable(true);
        FileWriter fw = new FileWriter(sh);
        BufferedWriter bf = new BufferedWriter(fw);
        String[] hiveCommond = {"originPath=$1", "targetPath=$2", "echo ${originPath}", "echo ${targetPath}", "hdfs dfs -put ${originPath} ${targetPath}"};
        for (int i = 0; i < hiveCommond.length; i++) {
            bf.write(hiveCommond[i]);
            if (i < hiveCommond.length - 1) {
                bf.newLine();
            }
        }
        bf.flush();
        bf.close();
    }

    public static void main(String[] args) throws Exception {
        File file = new File(NDateUtil.getDays());
        ShellUtil.createExcuteHiveShell(file.getPath(), "O_LDM_DSR");
        ShellUtil.createPutHiveShell(file.getPath(), "O_LDM_DSR");
    }
}
