package pdm.bdpj.hivetools.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataTypeMappingBo implements Serializable {

    private static final long serialVersionUID = -1;

    private String srcDbType;

    private String srcTypeCd;

    private String tgtDbType;

    private String tgtTypeCd;
}
