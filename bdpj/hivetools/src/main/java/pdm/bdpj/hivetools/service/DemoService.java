package pdm.bdpj.hivetools.service;

import pdm.bdpj.hivetools.model.bo.DemoBo;

import java.util.ArrayList;

public interface DemoService {

    ArrayList<String> find();

    int save(DemoBo user);
}
