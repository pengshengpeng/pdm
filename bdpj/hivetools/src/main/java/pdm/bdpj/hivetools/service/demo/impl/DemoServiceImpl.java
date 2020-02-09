package pdm.bdpj.hivetools.service.demo.impl;

import org.springframework.stereotype.Service;
import pdm.bdpj.common.constant.NHttpStatusEnum;
import pdm.bdpj.common.exception.ServiceException;
import pdm.bdpj.hivetools.model.bo.DemoBo;
import pdm.bdpj.hivetools.service.demo.DemoService;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public ArrayList<String> find() {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, "1111", "2222", "3333");
        return list;
    }

    @Override
    public int save(DemoBo user) {
        throw new ServiceException(NHttpStatusEnum.PARAMETER_ERROR);
    }
}
