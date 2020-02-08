package pdm.bdpj.hivetools.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pdm.bdpj.common.exception.ServiceException;
import pdm.bdpj.common.response.ResponseVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String defaultErrorCode = "501";
    private static final String defaultErrorMsg = "系统繁忙，请稍后再试";

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseVO serviceExceptionHandler(HttpServletRequest request, Exception e, HttpServletResponse response) {
        ResponseVO result = new ResponseVO<>();
        //如果是业务逻辑异常，返回具体的错误码与提示信息
        if (e instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) e;
            result.setCode(serviceException.getErrorCode());
            result.setMsg(serviceException.getErrorMsg());
            result.setSuccess(false);
        } else {
            //系统级异常，错误码固定为501，提示语固定为系统繁忙，请稍后再试，对系统级异常进行日志记录
            result.setCode(defaultErrorCode);
            result.setMsg(defaultErrorMsg);
            result.setSuccess(false);
            log.error("系统异常:{}", e.getMessage(), e);
        }
        return result;
    }
}
