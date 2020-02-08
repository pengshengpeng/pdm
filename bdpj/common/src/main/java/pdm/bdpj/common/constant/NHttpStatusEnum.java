package pdm.bdpj.common.constant;

public enum NHttpStatusEnum {

    OK(200, "000000", "操作成功"),
    SERVER_ERROR(500, "999999", "系统错误"),
    TOKEN_ERROR(401, "999997", "token未授权"),
    AUTH_ERROR(403, "999995", "没有权限，拒绝访问"),
    PARAMETER_ERROR(400, "012003", "请求参数类型错误");

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

    public String getMessage(){
        return this.message;
    }
}
