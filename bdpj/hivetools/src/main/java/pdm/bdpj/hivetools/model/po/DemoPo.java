package pdm.bdpj.hivetools.model.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class DemoPo implements Serializable {
    private Integer id;
    private String name;
    private int age;
    private String sex;
    private String address;
    private int math;
    private int english;
}
