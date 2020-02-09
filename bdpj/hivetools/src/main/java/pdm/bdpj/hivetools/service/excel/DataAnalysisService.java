package pdm.bdpj.hivetools.service.excel;

import java.util.List;

/**
 * @Author pengshengpeng
 * @Date 2020/2/9 17:16
 **/
public interface DataAnalysisService {

    /**
     * 创建新表
     *
     * @param list 表sql集合
     * @return 是否创建成功
     */
    boolean createTable(List<String> list);

    /**
     * 插入数据
     *
     * @param list 数据sql集合
     * @return 是否插入成功
     */
    boolean insertIntoTable(List<String> list);

}
