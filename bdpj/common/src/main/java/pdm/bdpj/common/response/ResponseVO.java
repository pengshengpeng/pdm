package pdm.bdpj.common.response;

import lombok.Data;
import pdm.bdpj.common.constant.NHttpStatusEnum;

import java.io.Serializable;

@Data
public class ResponseVO<T> implements Serializable {
    private static final long serialVersionUID = 1047656828922600983L;
    private static final String defaultCode = "000000";
    private static final String defaultMsg = "操作成功";

    private String code;
    private String msg;
    private Boolean success;
    private T data;

    public static <T> ResponseVO<T> ok() {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.success = Boolean.TRUE;
        vo.code = defaultCode;
        vo.msg = defaultMsg;
        return vo;
    }

    public static <T> ResponseVO<T> ok(T data) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.success = Boolean.TRUE;
        vo.code = defaultCode;
        vo.msg = defaultMsg;
        vo.data = data;
        return vo;
    }

    public static <T> ResponseVO<T> error(NHttpStatusEnum nHttpStatusEnum) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.success = Boolean.FALSE;
        vo.code = nHttpStatusEnum.getCode();
        vo.msg = nHttpStatusEnum.getMessage();
        return vo;
    }

    public static <T> ResponseVO<T> error(String code, String msg) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.success = Boolean.FALSE;
        vo.code = code;
        vo.msg = msg;
        return vo;
    }

}
