package pdm.bdpj.hivetools.model.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TableFieldInfoBo implements Serializable {

    private static final long serialVersionUID = -1;

    private String tableSpace;

    private String tableName;

    private String tableComment;

    private String fieldName;

    private String fieldComment;

    private String fieldType;
}
