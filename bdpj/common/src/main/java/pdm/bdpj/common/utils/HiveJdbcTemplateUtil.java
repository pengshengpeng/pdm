package pdm.bdpj.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author pengshengpeng
 * @Date 2020/2/9 16:44
 **/
@Slf4j
public class HiveJdbcTemplateUtil {
    @Autowired
    @Qualifier("hiveDruidTemplate")
    private JdbcTemplate hiveDruidTemplate;

    /**
     * 创建新表
     */
    @RequestMapping("/table/create")
    public boolean createTable(String sql) {
        try {
            hiveDruidTemplate.execute(sql);
            return true;
        } catch (DataAccessException dae) {
            log.error("Create table encounter an error:{} ", dae.getMessage());
            return false;
        }
    }

    /**
     * 示例：将Hive服务器本地文档中的数据加载到Hive表中
     */
    public String loadIntoTable() {
        String filepath = "/home/hadoop/user_sample.txt";
        String sql = "load data local inpath '" + filepath + "' into table user_sample";
        String result = "Load data into table successfully...";
        try {
            // hiveJdbcTemplate.execute(sql);
            hiveDruidTemplate.execute(sql);
        } catch (DataAccessException dae) {
            result = "Load data into table encounter an error: " + dae.getMessage();
            log.error(result);
        }
        return result;
    }

    /**
     * 示例：向Hive表中添加数据
     */
    @RequestMapping("/table/insert")
    public String insertIntoTable() {
        String sql = "INSERT INTO TABLE  user_sample(user_num,user_name,user_gender,user_age) VALUES(888,'Plum','M',32)";
        String result = "Insert into table successfully...";
        try {
            // hiveJdbcTemplate.execute(sql);
            hiveDruidTemplate.execute(sql);
        } catch (DataAccessException dae) {
            result = "Insert into table encounter an error: " + dae.getMessage();
            log.error(result);
        }
        return result;
    }

    /**
     * 示例：删除表
     */
    @RequestMapping("/table/delete")
    public String delete(String tableName) {
        String sql = "DROP TABLE IF EXISTS " + tableName;
        String result = "Drop table successfully...";
        log.info("Running: " + sql);
        try {
            // hiveJdbcTemplate.execute(sql);
            hiveDruidTemplate.execute(sql);
        } catch (DataAccessException dae) {
            result = "Drop table encounter an error: " + dae.getMessage();
            log.error(result);
        }
        return result;
    }

}
