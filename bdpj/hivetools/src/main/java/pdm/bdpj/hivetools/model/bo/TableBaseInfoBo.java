package pdm.bdpj.hivetools.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TableBaseInfoBo implements Serializable {

    private static final long serialVersionUID = -3573538646491669106L;

    private String srcDbType;

    private String tgtDbType;

    private String tableSpace;

    private String tableName;

    private String tableComment;

    private String fileFormat;

    private String rowFormat;

    private String location;
}
