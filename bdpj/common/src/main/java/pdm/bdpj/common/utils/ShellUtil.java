package pdm.bdpj.common.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * @Author pengshengpeng
 * @Date 2020/2/10 16:07
 **/
public class ShellUtil {

    //创建shell
    public static void createExcuteHiveShell(String path,String fileName) throws Exception {

        File sh = new File(path,fileName);
        if (sh.exists()) {
            sh.delete();
        }
        sh.createNewFile();
        sh.setExecutable(true);
        FileWriter fw = new FileWriter(sh);
        BufferedWriter bf = new BufferedWriter(fw);
        new
        for (int i = 0; i < strs.length; i++) {
            bf.write(strs[i]);

            if (i < strs.length - 1) {
                bf.newLine();
            }
        }
        bf.flush();
        bf.close();
    }


}
