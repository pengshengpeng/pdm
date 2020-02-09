package pdm.bdpj.hivetools.service.excel.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pdm.bdpj.common.constant.NHttpStatusEnum;
import pdm.bdpj.common.exception.ServiceException;
import pdm.bdpj.hivetools.service.excel.DataAnalysisService;

import java.util.List;

/**
 * @Author pengshengpeng
 * @Date 2020/2/9 17:19
 **/
@Slf4j
@Service
public class DataAnalysisServiceImpl implements DataAnalysisService {

    @Autowired
    private JdbcTemplate hiveDruidTemplate;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public boolean createTable(List<String> list) {
        if (list == null || list.size() <= 0) {
            throw new ServiceException(NHttpStatusEnum.SQL_DATA_EMPTY);
        }
        for (String sql : list) {
            try {
                hiveDruidTemplate.execute(sql);
            } catch (DataAccessException e) {
                log.error("创建表失败，sql:{}", sql, e.getMessage());
                return false;
            }
        }
        return true;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public boolean insertIntoTable(List<String> list) {
        if (list == null || list.size() <= 0) {
            throw new ServiceException(NHttpStatusEnum.SQL_DATA_EMPTY);
        }
        for (String sql : list) {
            try {
                hiveDruidTemplate.execute(sql);
            } catch (DataAccessException e) {
                log.error("插入表数据失败，sql:{}", sql, e.getMessage());
                return false;
            }
        }
        return true;
    }
}
