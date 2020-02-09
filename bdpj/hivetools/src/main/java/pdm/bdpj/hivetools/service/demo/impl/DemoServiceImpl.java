package pdm.bdpj.hivetools.service.demo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pdm.bdpj.common.constant.NHttpStatusEnum;
import pdm.bdpj.common.exception.ServiceException;
import pdm.bdpj.common.utils.NBeanUtil;
import pdm.bdpj.hivetools.mapper.DemoMapper;
import pdm.bdpj.hivetools.model.bo.DemoBo;
import pdm.bdpj.hivetools.model.po.DemoPo;
import pdm.bdpj.hivetools.service.demo.DemoService;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DemoMapper demoMapper;

    @Override
    public ArrayList<String> find() {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, "1111", "2222", "3333");
        return list;
    }

    @Override
    public int save(DemoBo user) throws Exception {
        DemoPo demoPo = NBeanUtil.populateBean(user, DemoPo.class);
        int i= demoMapper.insert(demoPo);
        if (i != 1) {
            throw new ServiceException(NHttpStatusEnum.INSERT_OPERATION_FAIL);
        }
        return i;
    }
}
