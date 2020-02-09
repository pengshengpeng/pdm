package pdm.bdpj.hivetools.controller.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pdm.bdpj.common.constant.NHttpStatusEnum;
import pdm.bdpj.common.response.ResponseVO;
import pdm.bdpj.hivetools.model.bo.DemoBo;
import pdm.bdpj.hivetools.service.demo.DemoService;
import pdm.bdpj.hivetools.service.excel.DataAnalysisService;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/demo")
public class DemoController extends ResponseVO {
    @Autowired
    private DemoService demoService;

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @GetMapping("/test1")
    public ResponseVO find() {
        ArrayList<String> list = demoService.find();
        dataAnalysisService.createTable(list);
        return ResponseVO.ok(list);
    }

    @GetMapping("/test2")
    public ResponseVO test2() {
        ResponseVO result = new ResponseVO<>();
        return ResponseVO.error(NHttpStatusEnum.AUTH_ERROR);
    }

    @GetMapping("/test3")
    public ResponseVO test3() throws Exception {
        ResponseVO result = new ResponseVO<>();
        DemoBo user = new DemoBo();
        user.setName("psp");
        user.setAge(18);
        user.setId(1);
        user.setSex("男");
        user.setAddress("浦东新区");
        user.setMath(100);
        user.setEnglish(100);
        int i = demoService.save(user);
        return ResponseVO.ok(i);
    }

}
