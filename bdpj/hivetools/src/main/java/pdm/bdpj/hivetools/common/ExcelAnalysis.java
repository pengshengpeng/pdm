package pdm.bdpj.hivetools.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import pdm.bdpj.common.constant.NHttpStatusEnum;
import pdm.bdpj.common.exception.ServiceException;
import pdm.bdpj.common.utils.NDateUtil;
import pdm.bdpj.hivetools.model.bo.DataTypeMappingBo;
import pdm.bdpj.hivetools.model.bo.TableBaseInfoBo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelAnalysis {

    public static final String TABLE_SHRRT = "TABLE_LIST";

    public static final String MAPPING_SHRRT = "DATA_TYPE_MAPPING";

    public ExcelAnalysis() {
        throw new Error("工具类不允许实例化！");
    }

    /**
     * 获取并解析excel文件，返回一个二维集合
     *
     * @param file 上传的文件
     * @return 二维集合（第一重集合为行，第二重集合为列，每一行包含该行的列集合，列集合包含该行的全部单元格的值）
     */
    public static List<String> analysis(MultipartFile file) throws Exception {
        if (file == null) {
            throw new ServiceException(NHttpStatusEnum.EXCEL_FILE_NOT_EXIST);
        }

        //获取文件名称
        String fileName = file.getOriginalFilename();
        log.info(String.format("开始解析Excel [%s] 文件名：[%s] 文件大小：[%d]", NDateUtil.getTime(), fileName, file.getSize()));

        List<String> tableCreateSqls = new ArrayList<>();
        try (
                InputStream in = file.getInputStream();
                Workbook workbook = judegExcelEdition(fileName) ? new XSSFWorkbook(in) : new HSSFWorkbook(in)
        ) {
            //获取data_type_mapping工作表
            Sheet mappingSheet = workbook.getSheet(MAPPING_SHRRT);
            if (mappingSheet == null) {
                throw new ServiceException(NHttpStatusEnum.EXCEL_DATA_TYPE_MAPPING_EMPTY);
            }
            List<DataTypeMappingBo> dataTypeMappingBos = getDataTypeMappingBos(mappingSheet);
            if (dataTypeMappingBos == null || dataTypeMappingBos.size() == 0) {
                throw new ServiceException(NHttpStatusEnum.EXCEL_DATA_TYPE_MAPPING_EMPTY);
            }

            //获取table_list工作表
            Sheet tablesSheet = workbook.getSheet(TABLE_SHRRT);
            if (tablesSheet == null) {
                throw new ServiceException(NHttpStatusEnum.EXCEL_TABLE_LIST_EMPTY);
            }
            List<TableBaseInfoBo> tableBaseInfoBos = getTableBaseInfoBos(tablesSheet);
            if (tableBaseInfoBos == null || tableBaseInfoBos.size() == 0) {
                throw new ServiceException(NHttpStatusEnum.EXCEL_TABLE_LIST_EMPTY);
            }

            //获取table_create_sql语句
            for (TableBaseInfoBo tableBaseInfoBo : tableBaseInfoBos) {
                Sheet sheetFields = workbook.getSheet(tableBaseInfoBo.getTableName());
                if (sheetFields == null) {
                    throw new ServiceException(NHttpStatusEnum.EXCEL_SHEET_NOT_EXIST, tableBaseInfoBo.getTableName());
                }
                String tableCreateSql = getTableCreateSql(tableBaseInfoBo, sheetFields, dataTypeMappingBos);
                log.info(String.format("表名: [%s].[%s] ----建表语句：[%s]", tableBaseInfoBo.getTableSpace(), tableBaseInfoBo.getTableName(), tableCreateSql));
                tableCreateSqls.add(tableCreateSql);
            }
        }
        return tableCreateSqls;
    }

    /**
     * 判断上传的excel文件版本（xls为2003，xlsx为2017）
     *
     * @param fileName 文件路径
     * @return excel2007及以上版本返回true，excel2007以下版本返回false
     */
    private static boolean judegExcelEdition(String fileName) {
        return !fileName.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * 解析字段类型映射关系
     *
     * @param mappingSheet 映射工作簿
     * @return 字段类型映射信息
     */
    private static List<DataTypeMappingBo> getDataTypeMappingBos(Sheet mappingSheet) {
        List<DataTypeMappingBo> dataTypeMappingBos = new ArrayList<>();
        DataTypeMappingBo dataTypeMappingBo;
        //循环获取工作表的每一行
        for (int i = 1; i < mappingSheet.getPhysicalNumberOfRows(); i++) {
            Row sheetRow = mappingSheet.getRow(i);

            dataTypeMappingBo = new DataTypeMappingBo();
            dataTypeMappingBo.setSrcDbType(sheetRow.getCell(0).getStringCellValue());
            dataTypeMappingBo.setSrcTypeCd(sheetRow.getCell(1).getStringCellValue());
            dataTypeMappingBo.setTgtDbType(sheetRow.getCell(2).getStringCellValue());
            dataTypeMappingBo.setTgtTypeCd(sheetRow.getCell(3).getStringCellValue());

            dataTypeMappingBos.add(dataTypeMappingBo);
        }
        return dataTypeMappingBos;
    }

    /**
     * 解析表的基本信息
     *
     * @param tablesSheet 表信息工作簿
     * @return 表的基本信息
     */
    private static List<TableBaseInfoBo> getTableBaseInfoBos(Sheet tablesSheet) {
        List<TableBaseInfoBo> tableBaseInfoBos = new ArrayList<>();
        TableBaseInfoBo tableBaseInfoBo;
        //循环获取工作表的每一行
        for (int i = 1; i < tablesSheet.getPhysicalNumberOfRows(); i++) {
            Row sheetRow = tablesSheet.getRow(i);

            tableBaseInfoBo = new TableBaseInfoBo();
            tableBaseInfoBo.setSrcDbType(sheetRow.getCell(0).getStringCellValue());
            tableBaseInfoBo.setTgtDbType(sheetRow.getCell(1).getStringCellValue());
            tableBaseInfoBo.setTableSpace(sheetRow.getCell(2).getStringCellValue());
            tableBaseInfoBo.setTableName(sheetRow.getCell(3).getStringCellValue());
            tableBaseInfoBo.setTableComment(sheetRow.getCell(4).getStringCellValue());
            tableBaseInfoBo.setFileFormat(sheetRow.getCell(5).getStringCellValue());
            tableBaseInfoBo.setRowFormat(sheetRow.getCell(6).getStringCellValue());
            tableBaseInfoBo.setLocation(sheetRow.getCell(7).getStringCellValue());
            tableBaseInfoBo.setTgtLocation(sheetRow.getCell(8).getStringCellValue());
            tableBaseInfoBo.setZipFile(sheetRow.getCell(9).getStringCellValue());

            tableBaseInfoBos.add(tableBaseInfoBo);
        }
        return tableBaseInfoBos;
    }

    /**
     * 拼接建表语句
     *
     * @param tableBaseInfoBo    表信息
     * @param sheetFields        字段信息
     * @param dataTypeMappingBos 映射关系
     * @return 建表语句
     */
    private static String getTableCreateSql(TableBaseInfoBo tableBaseInfoBo, Sheet sheetFields, List<DataTypeMappingBo> dataTypeMappingBos) {

        return " ";
    }
}
