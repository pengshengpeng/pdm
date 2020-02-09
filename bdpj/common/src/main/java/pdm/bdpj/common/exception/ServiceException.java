package pdm.bdpj.common.exception;

import lombok.Data;
import pdm.bdpj.common.constant.NHttpStatusEnum;

@Data
public class ServiceException extends RuntimeException {
    /**
     * 异常信息
     */
    private String errorMsg;
    /**
     * 错误码
     */
    private String errorCode;

    public ServiceException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ServiceException(NHttpStatusEnum nHttpStatusEnum) {
        super(nHttpStatusEnum.getMessage());
        this.errorCode = nHttpStatusEnum.getCode();
        this.errorMsg = nHttpStatusEnum.getMessage();
    }

    public ServiceException(NHttpStatusEnum nHttpStatusEnum, String msg) {
        super(nHttpStatusEnum.getMessage());
        this.errorCode = nHttpStatusEnum.getCode();
        this.errorMsg = nHttpStatusEnum.getMessage() + msg;
    }
}
