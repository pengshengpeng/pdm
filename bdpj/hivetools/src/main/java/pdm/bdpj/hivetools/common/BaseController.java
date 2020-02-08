package pdm.bdpj.hivetools.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import pdm.bdpj.common.constant.NHttpStatusEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BaseController {
    public ModelAndView getModelAndView() {
        return new ModelAndView();
    }

    public HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

    public HttpSession getSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getSession();
    }

    public Map<String, Object> result(NHttpStatusEnum httpStatus) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", httpStatus.getCode());
        result.put("msg", httpStatus.getMessage());
        return result;
    }

    public Map<String, Object> result(NHttpStatusEnum httpStatus, List<?> data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", httpStatus.getCode());
        result.put("msg", httpStatus.getMessage());
        result.put("data", data);
        return result;
    }
}
