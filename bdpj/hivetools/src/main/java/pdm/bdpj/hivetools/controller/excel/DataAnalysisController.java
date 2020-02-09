package pdm.bdpj.hivetools.controller.excel;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pdm.bdpj.common.response.ResponseVO;
import pdm.bdpj.hivetools.common.ExcelAnalysis;

import java.util.List;

@RestController
@RequestMapping("/api/table/create")
public class DataAnalysisController extends ResponseVO {

    @PostMapping("/excel")
    public ResponseVO createTabelByExcel(MultipartFile file) throws Exception {
        List<String> analysis = ExcelAnalysis.analysis(file);
        return ResponseVO.ok(analysis);
    }
}
