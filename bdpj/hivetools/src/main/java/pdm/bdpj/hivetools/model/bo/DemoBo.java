package pdm.bdpj.hivetools.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DemoBo implements Serializable {
    private String userName;
    private int age;
}
