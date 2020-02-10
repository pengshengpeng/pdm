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
import pdm.bdpj.hivetools.model.bo.TableFieldInfoBo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelAnalysis {

    public static final String TABLE_SHRRT = "TABLE_LIST";

    public static final String MAPPING_SHRRT = "DATA_TYPE_MAPPING";

    public static final String HIVE_CONF = "${hiveconf:yyyymmdd}";

    public static final String ENCODING = "UTF-8";

    public ExcelAnalysis() {
        throw new Error("工具类不允许实例化！");
    }

    /**
     * 获取并解析excel文件，返回表创建SQL字符串
     *
     * @param file 上传的Excel
     * @return 多表创建SQL字符串
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
            File dir = new File(NDateUtil.getDays());
            if (!dir.exists()) {
                boolean mkdir = dir.mkdir();
                if (!mkdir) {
                    throw new ServiceException(NHttpStatusEnum.FILE_CREATE_FAIL);
                }
            }
            for (TableBaseInfoBo tableBaseInfoBo : tableBaseInfoBos) {
                Sheet sheetFields = workbook.getSheet(tableBaseInfoBo.getTableName());
                if (sheetFields == null) {
                    throw new ServiceException(NHttpStatusEnum.EXCEL_SHEET_NOT_EXIST, tableBaseInfoBo.getTableName());
                }
                //创建表语句 sql
                String tableCreateSql = getTableCreateSql(tableBaseInfoBo, sheetFields, dataTypeMappingBos);
                log.info(String.format("表名: [%s].[%s] ----建表语句：[%s]", tableBaseInfoBo.getTableSpace(), tableBaseInfoBo.getTableName(), tableCreateSql));
                File tableCreateSqlFile = new File(dir.getPath(), String.format("create_%s_%s.sql", tableBaseInfoBo.getTableName(), NDateUtil.getDays()));
                write(tableCreateSqlFile, ENCODING, tableCreateSql);

                //执行创建语句 sh
                String tableCreate = String.format("hive --hiveconf yyyymmdd=’’ -f %s", tableCreateSqlFile.getName());
                log.info(String.format("表名: [%s].[%s] ----执行建表语句：[%s]", tableBaseInfoBo.getTableSpace(), tableBaseInfoBo.getTableName(), tableCreate));
                File tableCreateFile = new File(dir.getPath(), String.format("exec_%s_%s.sh", tableBaseInfoBo.getTableName(), NDateUtil.getDays()));
                write(tableCreateFile, ENCODING, tableCreate);

                //执行加载语句 sh
                String tableLoadData = String.format("hive -e 'load data local inpath '%s' overwrite into table %s.%s_%s'", tableBaseInfoBo.getLocation(), tableBaseInfoBo.getTableSpace(), tableBaseInfoBo.getTableName(), HIVE_CONF);
                log.info(String.format("表名: [%s].[%s] ----执行加载语句：[%s]", tableBaseInfoBo.getTableSpace(), tableBaseInfoBo.getTableName(), tableLoadData));
                File tableLoadDataFile = new File(dir.getPath(), String.format("put_%s_%s.sh", tableBaseInfoBo.getTableName(), NDateUtil.getDays()));
                write(tableLoadDataFile, ENCODING, tableLoadData);

                //校验语句 sql
                String tableLoadCheckSql = String.format("select count(1) from %s.%s_%s;", tableBaseInfoBo.getTableSpace(), tableBaseInfoBo.getTableName(), HIVE_CONF);
                log.info(String.format("表名: [%s].[%s] ----校验语句：[%s]", tableBaseInfoBo.getTableSpace(), tableBaseInfoBo.getTableName(), tableLoadCheckSql));
                File tableLoadCheckSqlFile = new File(dir.getPath(), String.format("check_%s_%s.sql", tableBaseInfoBo.getTableName(), NDateUtil.getDays()));
                write(tableLoadCheckSqlFile, ENCODING, tableLoadCheckSql);

                tableCreateSqls.add(tableCreateSql);
            }
        }
        return tableCreateSqls;
    }

    /**
     * 写入文件
     *
     * @param file     文件
     * @param encoding 编码
     * @param content  内容
     * @throws Exception
     */
    private static void write(File file, String encoding, String content) throws Exception {
        try (
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8")
        ) {
            osw.write(content);
            osw.flush();
        }
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
            tableBaseInfoBo.setFields(sheetRow.getCell(6).getStringCellValue());
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
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("use %s;", tableBaseInfoBo.getTableSpace()));

        sb.append(String.format("drop table if exists %s.%s_%s;", tableBaseInfoBo.getTableSpace(), tableBaseInfoBo.getTableName(), HIVE_CONF));

        //拼接表字段
        List<TableFieldInfoBo> tableFieldInfoBos = new ArrayList<>();
        TableFieldInfoBo tableFieldInfoBo;
        for (int i = 1; i < sheetFields.getPhysicalNumberOfRows(); i++) {
            Row sheetRow = sheetFields.getRow(i);

            tableFieldInfoBo = new TableFieldInfoBo();
            tableFieldInfoBo.setTableSpace(sheetRow.getCell(0).getStringCellValue());
            tableFieldInfoBo.setTableName(sheetRow.getCell(1).getStringCellValue());
            tableFieldInfoBo.setTableComment(sheetRow.getCell(2).getStringCellValue());
            tableFieldInfoBo.setFieldName(sheetRow.getCell(3).getStringCellValue());
            tableFieldInfoBo.setFieldComment(sheetRow.getCell(4).getStringCellValue());
            tableFieldInfoBo.setFieldType(sheetRow.getCell(5).getStringCellValue());

            tableFieldInfoBos.add(tableFieldInfoBo);
        }
        String fields = getTableFieldsSql(mappingTableFieldsType(tableBaseInfoBo, tableFieldInfoBos, dataTypeMappingBos));

        //拼接表信息
        String location;
        if (tableBaseInfoBo.getTgtLocation().endsWith("/")) {
            location = tableBaseInfoBo.getTgtLocation() + NDateUtil.getDays() + "/" + tableBaseInfoBo.getTableName();
        } else {
            location = tableBaseInfoBo.getTgtLocation() + "/" + NDateUtil.getDays() + "/" + tableBaseInfoBo.getTableName();
        }
        String info = String.format("COMMENT '%s' row format delimited fields by '%s' stored as %s location '%s'",
                tableBaseInfoBo.getTableComment(), tableBaseInfoBo.getFields(), tableBaseInfoBo.getFileFormat(), location);

        //sql语句
        sb.append(String.format("create external table if not exists %s.%s_%s(%s)%s;", tableBaseInfoBo.getTableSpace(), tableBaseInfoBo.getTableName(), HIVE_CONF, fields, info));
        return sb.toString();
    }

    /**
     * 根据映射关系替换源表字段类型
     *
     * @param tableBaseInfoBo    表基本信息
     * @param tableFieldInfoBos  源表字段信息
     * @param dataTypeMappingBos 字段类型映射关系
     * @return 替换后的字段信息
     */
    private static List<TableFieldInfoBo> mappingTableFieldsType(TableBaseInfoBo tableBaseInfoBo, List<TableFieldInfoBo> tableFieldInfoBos, List<DataTypeMappingBo> dataTypeMappingBos) {
        for (TableFieldInfoBo tableFieldInfoBo : tableFieldInfoBos) {
            boolean flag = false;

            String fieldType = tableFieldInfoBo.getFieldType();
            int i = fieldType.indexOf('(');
            if (i != -1) {
                fieldType = fieldType.substring(0, i);
            }
            for (DataTypeMappingBo dataTypeMappingBo : dataTypeMappingBos) {
                //源数据库类型与目标数据库类型一致
                if (tableBaseInfoBo.getSrcDbType().equals(dataTypeMappingBo.getSrcDbType()) && tableBaseInfoBo.getTgtDbType().equals(dataTypeMappingBo.getTgtDbType())) {
                    //字段类型与源数据库字段类型一致，替换成目标数据库
                    if (fieldType.equals(dataTypeMappingBo.getSrcTypeCd())) {
                        tableFieldInfoBo.setFieldType(dataTypeMappingBo.getTgtTypeCd());
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                throw new ServiceException(NHttpStatusEnum.EXCEL_TABLE_FIELD_NOT_MATCH, String.format("库名：%s 表名：%s 字段名：%s 源数据库类型：%s 目标数据库类型：%s 源数据库字段类型：%s",
                        tableBaseInfoBo.getTableSpace(), tableBaseInfoBo.getTableName(), tableFieldInfoBo.getFieldName(), tableBaseInfoBo.getSrcDbType(), tableBaseInfoBo.getTgtDbType(), tableFieldInfoBo.getFieldType()));
            }
        }
        return tableFieldInfoBos;
    }

    /**
     * 获取表字段sql
     *
     * @param tableFieldInfoBos 表字段信息
     * @return 表字段sql
     */
    private static String getTableFieldsSql(List<TableFieldInfoBo> tableFieldInfoBos) {
        StringBuilder sb = new StringBuilder();
        for (TableFieldInfoBo tableFieldInfoBo : tableFieldInfoBos) {
            if (sb.length() == 0) {
                sb.append(String.format("%s %s COMMENT '%s'", tableFieldInfoBo.getFieldName(), tableFieldInfoBo.getFieldType(), tableFieldInfoBo.getFieldComment()));
            } else {
                sb.append(String.format(" ,%s %s COMMENT '%s'", tableFieldInfoBo.getFieldName(), tableFieldInfoBo.getFieldType(), tableFieldInfoBo.getFieldComment()));
            }
        }
        return sb.toString();
    }

}
