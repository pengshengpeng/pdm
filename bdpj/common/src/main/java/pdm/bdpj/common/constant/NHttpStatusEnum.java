package pdm.bdpj.common.constant;

public enum NHttpStatusEnum {

    /**
     * 基本错误
     */
    OK(200, "000000", "操作成功"),
    SERVER_ERROR(500, "000001", "系统错误"),
    TOKEN_ERROR(401, "000002", "token未授权"),
    AUTH_ERROR(403, "000003", "没有权限，拒绝访问"),
    PARAMETER_ERROR(400, "000004", "请求参数类型错误"),


    /**
     * 文件错误
     */
    EXCEL_TABLE_LIST_EMPTY(500, "100000", "Excel表信息数据为空"),
    EXCEL_DATA_TYPE_MAPPING_EMPTY(500, "100001", "Excel映射关系表为空"),
    EXCEL_SHEET_NOT_EXIST(500, "100002", "Excel表字段信息数据为空"),
    EXCEL_FILE_NOT_EXIST(500, "100003", "Excel文件不存在"),

    SQL_DATA_EMPTY(501, "200001", "sql数据为空");

    private final int status;
    private final String code;
    private final String message;

    private NHttpStatusEnum(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return this.status;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
