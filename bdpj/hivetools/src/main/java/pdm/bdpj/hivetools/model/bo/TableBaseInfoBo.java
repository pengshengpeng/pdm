package pdm.bdpj.hivetools.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TableBaseInfoBo implements Serializable {

    private static final long serialVersionUID = -1;

    private String srcDbType;

    private String tgtDbType;

    private String tableSpace;

    private String tableName;

    private String tableComment;

    private String fileFormat;

    private String rowFormat;

    private String location;

    private String tgtLocation;

    private String zipFile;
}
